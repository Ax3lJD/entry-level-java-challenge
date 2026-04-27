package com.challenge.api.dto;
import java.time.Instant;

public record CreateEmployeeRequest(
        String firstName,
        String lastName,
        Integer salary,
        Integer age,
        String jobTitle,
        String email,
        Instant contractHireDate) {}
