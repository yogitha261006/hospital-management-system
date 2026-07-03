package com.hospital.management.controller;

import com.hospital.management.dto.CreatePatientRequest;
import com.hospital.management.dto.PatientDTO;
import com.hospital.management.service.HospitalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final HospitalService hospitalService;

    public PatientController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDTO createPatient(@Valid @RequestBody CreatePatientRequest request) {
        return hospitalService.createPatient(request);
    }

    @GetMapping
    public List<PatientDTO> getAllPatients() {
        return hospitalService.getAllPatients();
    }

    @GetMapping("/{id}")
    public PatientDTO getPatient(@PathVariable Long id) {
        return hospitalService.getPatientById(id);
    }

    @PostMapping("/{id}/admit")
    public PatientDTO admitPatient(
            @PathVariable Long id, 
            @RequestParam String ward, 
            @RequestParam String bed) {
        return hospitalService.admitPatient(id, ward, bed);
    }

    @PostMapping("/{id}/discharge")
    public PatientDTO dischargePatient(@PathVariable Long id) {
        return hospitalService.dischargePatient(id);
    }

    @PostMapping("/{id}/assign/{staffId}")
    public PatientDTO assignDoctor(@PathVariable Long id, @PathVariable Long staffId) {
        return hospitalService.assignDoctorToPatient(id, staffId);
    }
}
