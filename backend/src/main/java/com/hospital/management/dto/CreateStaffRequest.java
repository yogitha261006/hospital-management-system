package com.hospital.management.dto;

import com.hospital.management.entity.StaffRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateStaffRequest {
    @NotBlank(message = "Employee code is required")
    private String employeeCode;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Role is required")
    private StaffRole role;

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public StaffRole getRole() { return role; }
    public void setRole(StaffRole role) { this.role = role; }
}
