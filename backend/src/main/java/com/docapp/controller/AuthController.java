package com.docapp.controller;

import com.docapp.domain.Role;
import jakarta.validation.Valid;
import com.docapp.domain.Gender;
import com.docapp.dto.AuthDtos.LoginRequest;
import com.docapp.dto.AuthDtos.LoginResponse;
import com.docapp.dto.AuthDtos.RegisterDoctorRequest;
import com.docapp.dto.AuthDtos.RegisterPatientRequest;
import com.docapp.entity.DoctorProfile;
import com.docapp.entity.PatientProfile;
import com.docapp.entity.UserAccount;
import com.docapp.repo.DoctorProfileRepository;
import com.docapp.repo.PatientProfileRepository;
import com.docapp.repo.UserAccountRepository;
import com.docapp.service.PasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserAccountRepository users;
    private final DoctorProfileRepository doctors;
    private final PatientProfileRepository patients;
    private final PasswordService passwords;

    public AuthController(UserAccountRepository users, DoctorProfileRepository doctors, PatientProfileRepository patients, PasswordService passwords) {
        this.users = users;
        this.doctors = doctors;
        this.patients = patients;
        this.passwords = passwords;
    }

    @PostMapping("/register/patient")
    @Transactional
    public ResponseEntity<?> registerPatient(@Valid @RequestBody RegisterPatientRequest req) {
        UserAccount u = new UserAccount();
        u.setName(req.name);
        u.setEmail(req.email);
        u.setPassword(passwords.hash(req.password));
        u.setRole(Role.PATIENT);
        u.setAge(req.age);
        u.setPhone(req.phone);
        if (req.gender != null && !req.gender.isBlank()) u.setGender(Gender.valueOf(req.gender.toUpperCase()));
        users.save(u);
        PatientProfile p = new PatientProfile();
        p.setUser(u);
        patients.save(p);
        return ResponseEntity.ok(u.getId());
    }

    @PostMapping("/register/doctor")
    @Transactional
    public ResponseEntity<?> registerDoctor(@Valid @RequestBody RegisterDoctorRequest req) {
        UserAccount u = new UserAccount();
        u.setName(req.name);
        u.setEmail(req.email);
        u.setPassword(passwords.hash(req.password));
        u.setRole(Role.DOCTOR);
        u.setPhone(req.phone);
        if (req.age != null) u.setAge(req.age);
        if (req.gender != null && !req.gender.isBlank()) u.setGender(Gender.valueOf(req.gender.toUpperCase()));
        users.save(u);

        DoctorProfile d = new DoctorProfile();
        d.setUser(u);
        d.setDegrees(req.degrees);
        d.setExperienceYears(req.experienceYears);
        d.setSpecialization(req.specialization);
        d.setCity(req.city);
        d.setClinicLocation(req.clinicLocation);
        if (req.gender != null && !req.gender.isBlank()) d.setGender(Gender.valueOf(req.gender.toUpperCase()));
        doctors.save(d);

        return ResponseEntity.ok(d.getId());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        return users.findByEmail(req.email).filter(u -> passwords.matches(req.password, u.getPassword())).map(u -> {
            if (u.getToken() == null || u.getToken().isEmpty()) {
                u.setToken(UUID.randomUUID().toString());
                users.save(u);
            }
            LoginResponse r = new LoginResponse();
            r.userId = u.getId();
            r.name = u.getName();
            r.email = u.getEmail();
            r.role = u.getRole().name();
            r.token = u.getToken();
            return ResponseEntity.ok(r);
        }).orElse(ResponseEntity.status(401).build());
    }
}