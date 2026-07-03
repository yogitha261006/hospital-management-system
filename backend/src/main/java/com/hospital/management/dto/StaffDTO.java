package com.hospital.management.dto;

import com.hospital.management.entity.Staff;
import com.hospital.management.entity.StaffRole;

public class StaffDTO {
    private final Long staffId;
    private final String employeeCode;
    private final String name;
    private final StaffRole role;
    private final boolean availability;
    private int load; // patients assigned

    public StaffDTO(Staff staff) {
        this.staffId = staff.getStaffId();
        this.employeeCode = staff.getEmployeeCode();
        this.name = staff.getName();
        this.role = staff.getRole();
        this.availability = staff.isAvailable();
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public Long getStaffId() { return staffId; }
    public String getEmployeeCode() { return employeeCode; }
    public String getName() { return name; }
    public StaffRole getRole() { return role; }
    public boolean isAvailable() { return availability; }
    public int getLoad() { return load; }
}
