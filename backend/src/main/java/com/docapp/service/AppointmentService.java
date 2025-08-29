package com.docapp.service;

import com.docapp.domain.AppointmentStatus;
import com.docapp.entity.Appointment;
import com.docapp.repo.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AppointmentService {
    private final AppointmentRepository appointments;

    public AppointmentService(AppointmentRepository appointments) {
        this.appointments = appointments;
    }

    public void accept(Long id) {
        Appointment a = appointments.findById(id).orElseThrow();
        if (a.getStatus() != AppointmentStatus.PENDING) throw new IllegalStateException();
        a.setStatus(AppointmentStatus.CONFIRMED);
    }

    public void cancel(Long id) {
        Appointment a = appointments.findById(id).orElseThrow();
        if (a.getStatus() != AppointmentStatus.PENDING) throw new IllegalStateException();
        Instant now = Instant.now();
        if (!now.isBefore(a.getStartTime())) throw new IllegalStateException();
        a.setStatus(AppointmentStatus.CANCELLED);
    }
}