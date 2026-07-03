package com.hospital.management.controller;

import com.hospital.management.dto.CreateStaffRequest;
import com.hospital.management.dto.StaffDTO;
import com.hospital.management.service.HospitalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staff")
public class StaffController {

    private final HospitalService hospitalService;

    public StaffController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StaffDTO createStaff(@Valid @RequestBody CreateStaffRequest request) {
        return hospitalService.createStaff(request);
    }

    @GetMapping
    public List<StaffDTO> getAllStaff() {
        return hospitalService.getAllStaff();
    }

    @PostMapping("/{id}/availability")
    public StaffDTO setAvailability(@PathVariable Long id, @RequestParam boolean available) {
        return hospitalService.setStaffAvailability(id, available);
    }
}
