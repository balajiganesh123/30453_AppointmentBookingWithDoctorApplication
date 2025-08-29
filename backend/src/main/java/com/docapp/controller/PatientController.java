package com.docapp.controller;
import com.docapp.repo.PatientProfileRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientProfileRepository patients;
    public PatientController(PatientProfileRepository patients) { this.patients = patients; }
    @GetMapping
    public ResponseEntity<?> list() { return ResponseEntity.ok(patients.findAll()); }
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id) { return ResponseEntity.of(patients.findById(id)); }
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<?> byUser(@PathVariable("userId") Long userId) { return ResponseEntity.of(patients.findByUserId(userId)); }
}
