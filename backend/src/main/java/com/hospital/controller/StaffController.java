package com.hospital.controller;

import com.hospital.enums.Gender;
import com.hospital.exception.HospitalException;
import com.hospital.model.Doctor;
import com.hospital.model.Nurse;
import com.hospital.service.StaffService;
import com.hospital.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    private final StaffService staffService;
    private final ReportService reportService;

    public StaffController(StaffService staffService, ReportService reportService) {
        this.staffService = staffService;
        this.reportService = reportService;
    }

    @GetMapping("/doctors")
    public List<Doctor> getAllDoctors() {
        return reportService.getAllDoctors();
    }

    @GetMapping("/nurses")
    public List<Nurse> getAllNurses() {
        return reportService.getAllNurses();
    }

    @PostMapping("/doctors")
    public Doctor addDoctor(@RequestBody AddDoctorRequest request) throws HospitalException {
        return staffService.addDoctor(
                request.name, request.age, Gender.valueOf(request.gender.toUpperCase()), request.contact, request.specialization
        );
    }

    @PostMapping("/nurses")
    public Nurse addNurse(@RequestBody AddNurseRequest request) throws HospitalException {
        return staffService.addNurse(
                request.name, request.age, Gender.valueOf(request.gender.toUpperCase()), request.contact, request.shiftType
        );
    }

    @PostMapping("/{staffId}/assign-department/{departmentId}")
    public void assignToDepartment(@PathVariable String staffId, @PathVariable String departmentId) throws HospitalException {
        staffService.assignDepartment(staffId, departmentId);
    }

    static class AddDoctorRequest {
        public String name;
        public int age;
        public String gender;
        public String contact;
        public String specialization;
    }

    static class AddNurseRequest {
        public String name;
        public int age;
        public String gender;
        public String contact;
        public String shiftType;
    }
}
