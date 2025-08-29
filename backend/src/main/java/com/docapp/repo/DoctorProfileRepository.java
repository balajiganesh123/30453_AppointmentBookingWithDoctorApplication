package com.docapp.repo;

import com.docapp.entity.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {

    Optional<DoctorProfile> findByUserId(Long userId);

    @Query(
        value = "select * from doctor_profiles dp " +
                "where (:spec is null or lower(dp.specialization) like lower(concat('%', :spec, '%'))) " +
                "order by dp.id asc",
        nativeQuery = true
    )
    List<DoctorProfile> search(@Param("spec") String specialization);

    @Query("select distinct d.specialization from DoctorProfile d where d.specialization is not null order by d.specialization asc")
    List<String> findDistinctSpecializations();
}
