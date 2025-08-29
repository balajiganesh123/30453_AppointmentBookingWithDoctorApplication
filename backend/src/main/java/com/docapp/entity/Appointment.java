package com.docapp.entity;
import com.docapp.domain.AppointmentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.Instant;

@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Entity
@Table(name="appointments", uniqueConstraints = {@UniqueConstraint(columnNames = {"doctor_id","start_time"})})
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="doctor_id", nullable=false)
    private DoctorProfile doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="patient_id", nullable=false)
    private PatientProfile patient;

    @Column(name="start_time", nullable=false)
    private Instant startTime;

    @Column(name="end_time", nullable=false)
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private AppointmentStatus status;

    private String reason;
    private String notes;

    @PrePersist
    void onCreate() {
        if (status == null) status = AppointmentStatus.PENDING;
    }

    public Long getId() { return id; }
    public DoctorProfile getDoctor() { return doctor; }
    public void setDoctor(DoctorProfile doctor) { this.doctor = doctor; }
    public PatientProfile getPatient() { return patient; }
    public void setPatient(PatientProfile patient) { this.patient = patient; }
    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }
    public Instant getEndTime() { return endTime; }
    public void setEndTime(Instant endTime) { this.endTime = endTime; }
    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
