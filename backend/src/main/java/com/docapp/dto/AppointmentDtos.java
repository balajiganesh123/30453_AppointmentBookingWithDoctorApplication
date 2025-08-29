package com.docapp.dto;

import com.docapp.domain.AppointmentStatus;

public class AppointmentDtos {
    public static class CreateAppointmentRequest {
        public Long doctorId;
        public Long patientId;
        public String startUtc;
        public String reason;
        public String notes;
    }
    public static class UpdateStatusRequest {
        public AppointmentStatus status;
    }
    public static class DoctorListItem {
        public Long id;
        public String patientName;
        public String startUtc;
        public String endUtc;
        public String status;
    }
    public static class PatientListItem {
        public Long id;
        public String doctorName;
        public String startUtc;
        public String endUtc;
        public String status;
    }
}
