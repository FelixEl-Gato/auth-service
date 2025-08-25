package com.crediya.auth.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserEntity {
    @Id
    private Long id;
    private String name;
    @Column("last_name")
    private String lastName;
    @Column("birth_date")
    private LocalDate birthDate;
    private String address;
    private String phone;
    private String email;
    @Column("base_salary")
    private BigDecimal baseSalary;
}
