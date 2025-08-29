package com.docapp.controller;

import com.docapp.dto.DoctorDtos.*;
import com.docapp.domain.AppointmentStatus;
import com.docapp.entity.*;
import com.docapp.repo.*;
import com.docapp.service.SlotService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorProfileRepository doctors;
    private final DoctorScheduleRepository schedules;
    private final AppointmentRepository appointments;
    private final SlotService slotService;

    public DoctorController(DoctorProfileRepository doctors, DoctorScheduleRepository schedules, AppointmentRepository appointments, SlotService slotService) {
        this.doctors = doctors;
        this.schedules = schedules;
        this.appointments = appointments;
        this.slotService = slotService;
    }

    @GetMapping
    public ResponseEntity<?> search(@RequestParam(value = "specialization", required = false) String specialization) {
        String spec = specialization != null && !specialization.trim().isEmpty() ? specialization.trim() : null;
        return ResponseEntity.ok(doctors.search(spec));
    }

    @GetMapping("/specializations")
    public ResponseEntity<?> listSpecializations() {
        return ResponseEntity.ok(doctors.findDistinctSpecializations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id) {
        return ResponseEntity.of(doctors.findById(id).map(d -> d));
    }

    @PostMapping("/{id}/schedules")
    @Transactional
    public ResponseEntity<?> addSchedule(@PathVariable("id") Long id, @RequestBody ScheduleRequest req) {
        ZoneId zone = ZoneId.systemDefault();
        LocalTime start = LocalTime.parse(req.start);
        LocalTime end = LocalTime.parse(req.end);
        if (!end.isAfter(start)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Start time must be earlier than end time"));
        }
        Instant now = Instant.now();
        Instant startInstant = LocalDateTime.of(req.date, start).atZone(zone).toInstant();
        if (!startInstant.isAfter(now)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cannot add availability in the past"));
        }
        DoctorProfile d = doctors.findById(id).orElseThrow();
        DoctorSchedule s = new DoctorSchedule();
        s.setDoctor(d);
        s.setDate(req.date);
        s.setStartTime(start);
        s.setEndTime(end);
        s.setSlotMinutes(req.slotMinutes != null ? req.slotMinutes : 15);
        schedules.save(s);
        return ResponseEntity.ok(s.getId());
    }

    @GetMapping("/{id}/slots")
    public ResponseEntity<?> slots(@PathVariable("id") Long id, @RequestParam("date") String date, @RequestParam(value = "tz", required = false) String tz) {
        ZoneId zone = tz != null ? ZoneId.of(tz) : ZoneOffset.UTC;
        LocalDate d = LocalDate.parse(date);
        List<DoctorSchedule> ds = schedules.findByDoctorIdAndDate(id, d);
        Instant dayStart = d.atStartOfDay(zone).toInstant();
        Instant dayEnd = d.plusDays(1).atStartOfDay(zone).toInstant();
        List<Appointment> taken = appointments.findTakenSlots(id, dayStart, dayEnd, Arrays.asList(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED));
        Set<String> seen = new HashSet<>();
        List<Map<String, String>> out = new ArrayList<>();
        Instant now = Instant.now();
        for (DoctorSchedule s : ds) {
            for (Instant[] p : slotService.buildSlots(s, zone)) {
                Instant s0 = p[0];
                Instant s1 = p[1];
                if (!s0.isAfter(now)) continue;
                boolean conflict = false;
                for (Appointment a : taken) {
                    if (s0.isBefore(a.getEndTime()) && s1.isAfter(a.getStartTime())) {
                        conflict = true;
                        break;
                    }
                }
                if (conflict) continue;
                String key = s0.toString() + "|" + s1.toString();
                if (seen.add(key)) out.add(Map.of("startUtc", s0.toString(), "endUtc", s1.toString()));
            }
        }
        out.sort(Comparator.comparing(m -> Instant.parse(m.get("startUtc"))));
        return ResponseEntity.ok(out);
    }

    @GetMapping("/{id}/appointments/today")
    public ResponseEntity<?> todayQueue(@PathVariable("id") Long id, @RequestParam(value = "tz", required = false) String tz) {
        ZoneId zone = tz != null ? ZoneId.of(tz) : ZoneOffset.UTC;
        LocalDate today = LocalDate.now(zone);
        Instant from = today.atStartOfDay(zone).toInstant();
        Instant to = today.plusDays(1).atStartOfDay(zone).toInstant();
        return ResponseEntity.ok(appointments.findForDoctorBetween(id, from, to));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<?> byUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.of(doctors.findByUserId(userId));
    }
}