package com.docapp.service;
import com.docapp.entity.DoctorSchedule;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.*;
@Service
public class SlotService {
    public List<Instant[]> buildSlots(DoctorSchedule s, ZoneId zone) {
        List<Instant[]> out = new ArrayList<>();
        LocalDate d = s.getDate();
        LocalTime st = s.getStartTime();
        LocalTime et = s.getEndTime();
        int mins = s.getSlotMinutes()==null?15:s.getSlotMinutes();
        ZonedDateTime zStart = ZonedDateTime.of(d, st, zone);
        ZonedDateTime zEnd = ZonedDateTime.of(d, et, zone);
        while (!zStart.plusMinutes(mins).isAfter(zEnd)) {
            Instant a = zStart.toInstant();
            Instant b = zStart.plusMinutes(mins).toInstant();
            out.add(new Instant[]{a,b});
            zStart = zStart.plusMinutes(mins);
        }
        return out;
    }
}
