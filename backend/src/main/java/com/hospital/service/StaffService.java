package com.hospital.service;

import com.hospital.enums.Gender;
import com.hospital.exception.DepartmentException;
import com.hospital.exception.HospitalException;
import com.hospital.exception.StaffAssignmentException;
import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.model.Hospital;
import com.hospital.model.Nurse;
import com.hospital.model.Staff;
import com.hospital.util.HospitalValidator;

/**
 * Service responsible for staff registration and department assignment operations.
 *
 * <p>{@code StaffService} provides a high-level API for adding clinical staff
 * (doctors and nurses) to the hospital and linking them to departments. All
 * input is validated through {@link HospitalValidator} before any state
 * mutation occurs.</p>
 *
 * <p>This service delegates persistence and lookup to the underlying
 * {@link Hospital} model, and ensures that bidirectional relationships
 * (staff ↔ department) are kept consistent.</p>
 *
 * @author Hospital Management System
 * @version 1.0
 * @see Hospital
 * @see Doctor
 * @see Nurse
 * @see Department
 */
public class StaffService {

    /** The hospital instance this service operates on. */
    private final Hospital hospital;

    /**
     * Constructs a {@code StaffService} bound to the given hospital.
     *
     * @param hospital the {@link Hospital} instance to manage; must not be {@code null}
     */
    public StaffService(Hospital hospital) {
        this.hospital = hospital;
    }

    /**
     * Registers a new doctor in the hospital.
     *
     * <p>Validates all input fields, creates a new {@link Doctor} instance,
     * and adds the doctor to the hospital's staff registry.</p>
     *
     * @param name           the doctor's full name; must not be blank
     * @param age            the doctor's age; must be within valid range
     * @param gender         the doctor's {@link Gender}; must not be {@code null}
     * @param contactNumber  the doctor's contact number; must match expected format
     * @param specialization the doctor's area of medical specialization; must not be empty
     * @return the newly created and registered {@link Doctor}
     * @throws HospitalException if any validation check fails
     */
    public Doctor addDoctor(String name, int age, Gender gender,
                            String contactNumber, String specialization) throws HospitalException {
        HospitalValidator.validateName(name);
        HospitalValidator.validateAge(age);
        HospitalValidator.validateContactNumber(contactNumber);
        HospitalValidator.validateNotNull(gender, "Gender");
        HospitalValidator.validateNotEmpty(specialization, "Specialization");

        Doctor doctor = new Doctor(name, age, gender, contactNumber, specialization);
        hospital.addStaff(doctor);
        return doctor;
    }

    /**
     * Registers a new nurse in the hospital.
     *
     * <p>Validates all input fields, creates a new {@link Nurse} instance,
     * and adds the nurse to the hospital's staff registry.</p>
     *
     * @param name          the nurse's full name; must not be blank
     * @param age           the nurse's age; must be within valid range
     * @param gender        the nurse's {@link Gender}; must not be {@code null}
     * @param contactNumber the nurse's contact number; must match expected format
     * @param shiftType     the nurse's shift type (e.g., "DAY", "NIGHT"); must not be empty
     * @return the newly created and registered {@link Nurse}
     * @throws HospitalException if any validation check fails
     */
    public Nurse addNurse(String name, int age, Gender gender,
                          String contactNumber, String shiftType) throws HospitalException {
        HospitalValidator.validateName(name);
        HospitalValidator.validateAge(age);
        HospitalValidator.validateContactNumber(contactNumber);
        HospitalValidator.validateNotNull(gender, "Gender");
        HospitalValidator.validateNotEmpty(shiftType, "Shift type");

        Nurse nurse = new Nurse(name, age, gender, contactNumber, shiftType);
        hospital.addStaff(nurse);
        return nurse;
    }

    /**
     * Assigns a staff member to a department.
     *
     * <p>Establishes a bidirectional link: the department records the staff
     * member's ID and the staff member records the department ID. Both the
     * staff member and the department must already exist in the hospital.</p>
     *
     * @param staffId      the unique identifier of the staff member
     * @param departmentId the unique identifier of the department
     * @throws StaffAssignmentException if no staff member with the given ID exists
     * @throws DepartmentException      if no department with the given ID exists or
     *                                  the department rejects the assignment
     * @throws HospitalException        if an unexpected hospital-level error occurs
     */
    public void assignDepartment(String staffId, String departmentId)
            throws StaffAssignmentException, DepartmentException, HospitalException {
        Staff staff = hospital.getStaff(staffId);
        Department department;
        try {
            department = hospital.getDepartment(departmentId);
        } catch (DepartmentException e) {
            throw e;
        }

        department.addStaff(staffId);
        staff.assignDepartment(departmentId);
    }

    /**
     * Retrieves a staff member by their unique identifier.
     *
     * @param staffId the unique identifier of the staff member
     * @return the {@link Staff} instance with the given ID
     * @throws StaffAssignmentException if no staff member with the given ID exists
     */
    public Staff getStaff(String staffId) throws StaffAssignmentException {
        return hospital.getStaff(staffId);
    }
}
