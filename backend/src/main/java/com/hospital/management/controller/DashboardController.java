package com.hospital.management.controller;

import com.hospital.management.dto.DashboardStatsDTO;
import com.hospital.management.service.HospitalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final HospitalService hospitalService;

    public DashboardController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping
    public DashboardStatsDTO getDashboardStats() {
        return hospitalService.getDashboardStats();
    }
}
