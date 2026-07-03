package com.hospital.enums;

public enum StaffRole {
    DOCTOR("Doctor"),
    NURSE("Nurse"),
    ADMIN("Administrator"),
    TECHNICIAN("Technician");

    private final String displayName;

    StaffRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
