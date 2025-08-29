package com.docapp.repo;
import com.docapp.entity.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    List<DoctorSchedule> findByDoctorIdAndDate(Long doctorId, LocalDate date);
    List<DoctorSchedule> findByDoctorIdAndDateBetween(Long doctorId, LocalDate from, LocalDate to);
}
