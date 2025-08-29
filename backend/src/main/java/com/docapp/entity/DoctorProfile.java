package com.docapp.entity;

import com.docapp.domain.Gender;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Entity
@Table(name="doctor_profiles", indexes = {@Index(columnList="specialization"), @Index(columnList="city")})
public class DoctorProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="user_id", nullable=false)
    private UserAccount user;

    private String degrees;
    private Integer experienceYears;
    private String specialization;
    private String city;
    private String clinicLocation;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserAccount getUser() { return user; }
    public void setUser(UserAccount user) { this.user = user; }

    public String getDegrees() { return degrees; }
    public void setDegrees(String degrees) { this.degrees = degrees; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getClinicLocation() { return clinicLocation; }
    public void setClinicLocation(String clinicLocation) { this.clinicLocation = clinicLocation; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
}
