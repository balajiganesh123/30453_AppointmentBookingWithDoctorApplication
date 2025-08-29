package com.docapp.dto;

import java.time.LocalDate;

public class DoctorDtos {
    public static class DoctorSearchRequest {
        public String specialization;
        public String city;
        public String gender;
        public Integer minExperience;
    }
    public static class ScheduleRequest {
        public LocalDate date;
        public String start;
        public String end;
        public Integer slotMinutes;
    }
    public static class BreakRequest {
        public LocalDate date;
        public String start;
        public String end;
    }
    public static class LeaveRequest {
        public LocalDate date;
    }
    public static class Slot {
        public String startUtc;
        public String endUtc;
    }
}
