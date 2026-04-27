package com.challenge.api.service;

import com.challenge.api.dto.CreateEmployeeRequest;
import com.challenge.api.model.Employee;
import com.challenge.api.model.EmployeeImpl;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmployeeService {

    private final Map<UUID, Employee> employees = new ConcurrentHashMap<>();

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employees.values());
    }

    public Employee getEmployeeByUuid(UUID uuid) {
        Employee employee = employees.get(uuid);
        if (employee == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found: " + uuid);
        }
        return employee;
    }

    public Employee createEmployee(CreateEmployeeRequest request) {
        validate(request);
        Instant hireDate = request.contractHireDate() != null ? request.contractHireDate() : Instant.now();
        Employee employee = buildEmployee(
                request.firstName(),
                request.lastName(),
                request.salary(),
                request.age(),
                request.jobTitle(),
                request.email(),
                hireDate);
        return save(employee);
    }

    private Employee save(Employee employee) {
        employees.put(employee.getUuid(), employee);
        return employee;
    }

    private static Employee buildEmployee(
            String firstName,
            String lastName,
            Integer salary,
            Integer age,
            String jobTitle,
            String email,
            Instant hireDate) {
        return new EmployeeImpl(
                UUID.randomUUID(),
                firstName,
                lastName,
                firstName + " " + lastName,
                salary,
                age,
                jobTitle,
                email,
                hireDate,
                null);
    }

    private static void validate(CreateEmployeeRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        }
        if (isBlank(request.firstName())
                || isBlank(request.lastName())
                || isBlank(request.jobTitle())
                || isBlank(request.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "firstName, lastName, jobTitle, and email are required");
        }
        if (request.salary() == null || request.salary() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "salary must be a non-negative integer");
        }
        if (request.age() == null || request.age() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "age must be a non-negative integer");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
