package com.crediya.auth.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserCreateDTO(
        @NotBlank(message = "name is required")
        String name,
        @NotBlank(message = "lastName is required")
        String lastName,
        LocalDate birthDate,
        String address,
        String phone,
        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid email address")
        String email,
        @NotNull (message = "baseSalary is required")
        @DecimalMin(value = "0.01", message = "baseSalary must be greater than zero and less than 15,000,000")
        @DecimalMax(value = "15000000", message = "baseSalary must be greater than zero and less than 15,000,000")
        BigDecimal baseSalary) { }
