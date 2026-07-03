package com.hospital.controller;

import com.hospital.enums.Gender;
import com.hospital.exception.HospitalException;
import com.hospital.model.AuditEntry;
import com.hospital.model.Patient;
import com.hospital.service.AdmissionService;
import com.hospital.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final AdmissionService admissionService;
    private final ReportService reportService;

    public PatientController(AdmissionService admissionService, ReportService reportService) {
        this.admissionService = admissionService;
        this.reportService = reportService;
    }

    @GetMapping
    public List<Patient> getAllPatients() {
        return reportService.getHospital().getAllPatients();
    }

    @GetMapping("/{id}")
    public Patient getPatient(@PathVariable String id) throws HospitalException {
        return admissionService.getPatient(id);
    }

    @GetMapping("/{id}/audit")
    public List<AuditEntry> getAuditTrail(@PathVariable String id) throws HospitalException {
        return reportService.getPatientAdmissionHistory(id);
    }

    @PostMapping("/register")
    public Patient registerPatient(@RequestBody RegisterPatientRequest request) throws HospitalException {
        return admissionService.registerPatient(
                request.name, request.age, Gender.valueOf(request.gender.toUpperCase()), request.contact, request.address
        );
    }

    @PostMapping("/{id}/admit")
    public void admitPatient(@PathVariable String id) throws HospitalException {
        admissionService.admitPatient(id);
    }

    @PostMapping("/{id}/start-treatment")
    public void startTreatment(@PathVariable String id) throws HospitalException {
        admissionService.startTreatment(id);
    }

    @PostMapping("/{id}/discharge")
    public void dischargePatient(@PathVariable String id) throws HospitalException {
        admissionService.dischargePatient(id);
    }

    static class RegisterPatientRequest {
        public String name;
        public int age;
        public String gender;
        public String contact;
        public String address;
    }
}
