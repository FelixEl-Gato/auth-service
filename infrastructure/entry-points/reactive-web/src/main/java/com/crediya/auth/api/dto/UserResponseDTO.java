package com.crediya.auth.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserResponseDTO(
    Long id,
    String name,
    String lastName,
    LocalDate birthDate,
    String address,
    String phone,
    String email,
    BigDecimal baseSalary) { }
