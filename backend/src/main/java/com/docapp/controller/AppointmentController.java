package com.docapp.controller;
import com.docapp.domain.AppointmentStatus;
import com.docapp.dto.AppointmentDtos.CreateAppointmentRequest;
import com.docapp.entity.*;
import com.docapp.repo.*;
import com.docapp.service.AppointmentService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.*;
import java.util.*;
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentRepository appointments;
    private final DoctorProfileRepository doctors;
    private final PatientProfileRepository patients;
    private final DoctorScheduleRepository schedules;
    private final AppointmentService service;
    public AppointmentController(AppointmentRepository appointments, DoctorProfileRepository doctors, PatientProfileRepository patients, DoctorScheduleRepository schedules, AppointmentService service) {
        this.appointments = appointments;
        this.doctors = doctors;
        this.patients = patients;
        this.schedules = schedules;
        this.service = service;
    }
    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@RequestBody CreateAppointmentRequest req) {
        DoctorProfile d = doctors.findById(req.doctorId).orElseThrow();
        PatientProfile p = patients.findByUserId(req.patientId).orElseThrow();
        Instant start = Instant.parse(req.startUtc);
        LocalDate dateUtc = LocalDate.ofInstant(start, ZoneOffset.UTC);
        LocalDate dateIst = LocalDate.ofInstant(start, ZoneId.of("Asia/Kolkata"));
        List<DoctorSchedule> ds = schedules.findByDoctorIdAndDate(d.getId(), dateUtc);
        if (ds.isEmpty()) ds = schedules.findByDoctorIdAndDate(d.getId(), dateIst);
        if (ds.isEmpty()) return ResponseEntity.badRequest().build();
        int slotMinutes = ds.get(0).getSlotMinutes()==null?15:ds.get(0).getSlotMinutes();
        Instant end = start.plusSeconds(slotMinutes*60L);
        Instant now = Instant.now();
        if (!start.isAfter(now)) return ResponseEntity.status(409).build();
        Instant from = start.minus(Period.ofDays(1)).minusSeconds(1);
        Instant to = start.plus(Period.ofDays(1)).plusSeconds(1);
        List<Appointment> taken = appointments.findTakenSlots(d.getId(), from, to, Arrays.asList(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED));
        for (Appointment a: taken) {
            if (start.isBefore(a.getEndTime()) && end.isAfter(a.getStartTime())) return ResponseEntity.status(409).build();
        }
        try {
            Appointment a = new Appointment();
            a.setDoctor(d);
            a.setPatient(p);
            a.setStartTime(start);
            a.setEndTime(end);
            a.setStatus(AppointmentStatus.PENDING);
            a.setReason(req.reason);
            a.setNotes(req.notes);
            appointments.save(a);
            return ResponseEntity.ok(a.getId());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        }
    }
    @PutMapping("/{id}/accept")
    @Transactional
    public ResponseEntity<?> accept(@PathVariable("id") Long id) {
        try {
            service.accept(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).build();
        }
    }
    @PutMapping("/{id}/cancel")
    @Transactional
    public ResponseEntity<?> cancel(@PathVariable("id") Long id) {
        try {
            service.cancel(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).build();
        }
    }
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> patientAppointments(@PathVariable("patientId") Long patientId) {
        return ResponseEntity.ok(appointments.findByPatientUserId(patientId));
    }
    @DeleteMapping("/patient/{patientId}")
    @Transactional
    public ResponseEntity<?> deleteForPatient(@PathVariable("patientId") Long patientId) {
        appointments.deleteByPatientUserId(patientId);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/doctor/{doctorId}")
    @Transactional
    public ResponseEntity<?> deleteForDoctor(@PathVariable("doctorId") Long doctorId) {
        appointments.deleteByDoctorId(doctorId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/doctor-user/{userId}/upcoming")
    public ResponseEntity<?> upcomingForDoctorUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(appointments.upcomingForDoctorUser(userId, Instant.now()));
    }
    @GetMapping("/doctor-user/{userId}/all")
    public ResponseEntity<?> allForDoctorUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(appointments.findAllForDoctorUser(userId));
    }
    @DeleteMapping("/{id}/patient/{patientUserId}")
    @Transactional
    public ResponseEntity<?> deleteOne(@PathVariable("id") Long id, @PathVariable("patientUserId") Long patientUserId) {
        Appointment a = appointments.findById(id).orElseThrow();
        if (!a.getPatient().getUser().getId().equals(patientUserId)) return ResponseEntity.status(403).build();
        if (a.getStatus()!=AppointmentStatus.CANCELLED) return ResponseEntity.status(409).build();
        appointments.delete(a);
        return ResponseEntity.noContent().build();
    }
}