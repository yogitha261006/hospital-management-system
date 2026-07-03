package com.hospital.management.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "staff")
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long staffId;

    private String employeeCode;
    private String name;

    @Enumerated(EnumType.STRING)
    private StaffRole role;

    private boolean availability;

    protected Staff() {
        // JPA requires no-arg constructor
    }

    public Staff(String employeeCode, String name, StaffRole role) {
        this.employeeCode = employeeCode;
        this.name = name;
        this.role = role;
        this.availability = true; // Default available when created
    }

    // Encapsulated state transition methods
    public void markAvailable() {
        this.availability = true;
    }

    public void markUnavailable() {
        this.availability = false;
    }

    // Read-only accessors
    public Long getStaffId() {
        return staffId;
    }

    public String getName() {
        return name;
    }

    public StaffRole getRole() {
        return role;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public boolean isAvailable() {
        return availability;
    }
}
