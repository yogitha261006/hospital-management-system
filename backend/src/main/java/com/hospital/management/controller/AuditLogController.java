package com.hospital.management.controller;

import com.hospital.management.entity.AuditLog;
import com.hospital.management.service.HospitalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/audit-logs")
public class AuditLogController {

    private final HospitalService hospitalService;

    public AuditLogController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping
    public List<AuditLog> getAuditLogs() {
        return hospitalService.getAuditLogs();
    }
}
