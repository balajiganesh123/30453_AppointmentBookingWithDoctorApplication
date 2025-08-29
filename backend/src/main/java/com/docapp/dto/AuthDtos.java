package com.docapp.dto;

import jakarta.validation.constraints.*;

public class AuthDtos {
    public static class LoginRequest {
        public String email;
        public String password;
    }

    public static class LoginResponse {
        public Long userId;
        public String name;
        public String email;
        public String role;
        public String token;
    }

    public static class RegisterPatientRequest {
        @NotBlank
        @Pattern(regexp = "^[A-Za-z\\s]+$")
        public String name;

        @NotBlank
        @Pattern(regexp = "^(?!\\.)(?!.*\\.\\.)[A-Za-z0-9.]+(?<!\\.)@example\\.com$")
        public String email;

        @NotBlank
        public String password;

        @NotBlank
        @Pattern(regexp = "\\d{10}")
        public String phone;

        @NotNull
        @Min(1)
        @Max(100)
        public Integer age;

        public String gender;
    }

    public static class RegisterDoctorRequest {
        @NotBlank
        @Pattern(regexp = "^[A-Za-z\\s]+$")
        public String name;

        @NotBlank
        @Pattern(regexp = "^(?!\\.)(?!.*\\.\\.)[A-Za-z0-9.]+(?<!\\.)@example\\.com$")
        public String email;

        @NotBlank
        public String password;

        @NotBlank
        @Pattern(regexp = "\\d{10}")
        public String phone;

        @NotNull
        @Min(1)
        @Max(100)
        public Integer age;

        public String gender;
        public String degrees;
        public Integer experienceYears;
        public String specialization;
        public String city;
        public String clinicLocation;
    }
}