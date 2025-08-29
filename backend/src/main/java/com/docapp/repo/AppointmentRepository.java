package com.docapp.repo;
import com.docapp.domain.AppointmentStatus;
import com.docapp.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import java.time.Instant;
import java.util.List;
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("select a from Appointment a where a.patient.id=:patientId order by a.startTime desc")
    List<Appointment> findByPatient(@Param("patientId") Long patientId);
    @Query("select a from Appointment a where a.patient.user.id=:userId order by a.startTime desc")
    List<Appointment> findByPatientUserId(@Param("userId") Long userId);
    @Query("select a from Appointment a where a.doctor.id=:doctorId and a.startTime between :from and :to order by a.startTime asc")
    List<Appointment> findForDoctorBetween(@Param("doctorId") Long doctorId, @Param("from") Instant from, @Param("to") Instant to);
    @Query("select a from Appointment a where a.doctor.id=:doctorId and a.startTime>=:from order by a.startTime asc")
    List<Appointment> upcomingForDoctor(@Param("doctorId") Long doctorId, @Param("from") Instant from);
    @Query("select a from Appointment a where a.doctor.id=:doctorId and a.startTime between :from and :to and a.status in :statuses")
    List<Appointment> findTakenSlots(@Param("doctorId") Long doctorId, @Param("from") Instant from, @Param("to") Instant to, @Param("statuses") List<AppointmentStatus> statuses);
    @Modifying
    @Query("delete from Appointment a where a.patient.user.id=:userId")
    void deleteByPatientUserId(@Param("userId") Long userId);
    @Modifying
    @Query("delete from Appointment a where a.doctor.id=:doctorId")
    void deleteByDoctorId(@Param("doctorId") Long doctorId);
    @Query("select a from Appointment a where a.doctor.user.id=:userId and a.startTime>=:from order by a.startTime asc")
    List<Appointment> upcomingForDoctorUser(@Param("userId") Long userId, @Param("from") Instant from);
    @Query("select a from Appointment a where a.doctor.user.id=:userId order by a.startTime desc")
    List<Appointment> findAllForDoctorUser(@Param("userId") Long userId);
}
