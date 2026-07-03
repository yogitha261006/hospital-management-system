package com.hospital.controller;

import com.hospital.model.Department;
import com.hospital.model.Ward;
import com.hospital.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalPatients", reportService.getTotalPatientCount());
        summary.put("admittedPatients", reportService.getAdmittedPatients().size());
        summary.put("dischargedPatients", reportService.getDischargedPatients().size());
        summary.put("totalDoctors", reportService.getAllDoctors().size());
        summary.put("totalNurses", reportService.getAllNurses().size());
        summary.put("availableBeds", reportService.getTotalAvailableBeds());
        return summary;
    }

    @GetMapping("/wards")
    public List<Ward> getAllWards() {
        return reportService.getHospital().getAllWards();
    }

    @GetMapping("/departments")
    public List<Department> getAllDepartments() {
        return reportService.getHospital().getAllDepartments();
    }
}
