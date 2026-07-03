package com.hospital.management.dto;

public class DashboardStatsDTO {
    private final long totalPatients;
    private final long admittedPatients;
    private final long dischargedPatients;
    private final long availableStaff;

    public DashboardStatsDTO(long totalPatients, long admittedPatients, long dischargedPatients, long availableStaff) {
        this.totalPatients = totalPatients;
        this.admittedPatients = admittedPatients;
        this.dischargedPatients = dischargedPatients;
        this.availableStaff = availableStaff;
    }

    public long getTotalPatients() { return totalPatients; }
    public long getAdmittedPatients() { return admittedPatients; }
    public long getDischargedPatients() { return dischargedPatients; }
    public long getAvailableStaff() { return availableStaff; }
}
