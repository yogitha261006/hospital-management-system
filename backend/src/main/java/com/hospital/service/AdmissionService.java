package com.hospital.service;

import com.hospital.enums.Gender;
import com.hospital.exception.AlreadyAdmittedException;
import com.hospital.exception.HospitalException;
import com.hospital.exception.InvalidPatientStateException;
import com.hospital.exception.PatientNotFoundException;
import com.hospital.exception.StaffAssignmentException;
import com.hospital.model.Doctor;
import com.hospital.model.Hospital;
import com.hospital.model.Patient;
import com.hospital.model.Staff;
import com.hospital.model.Ward;
import com.hospital.util.HospitalValidator;

/**
 * Service responsible for all patient lifecycle operations within the hospital.
 *
 * <p>{@code AdmissionService} acts as a facade over {@link Hospital} and
 * {@link Patient}, orchestrating registration, admission, treatment,
 * discharge, and resource-assignment workflows. It ensures that cross-cutting
 * concerns—such as ward-bed management and doctor–patient linkage—are handled
 * atomically from the caller's perspective.</p>
 *
 * <p>All mutating methods enforce validation via {@link HospitalValidator}
 * before delegating to the underlying model objects.</p>
 *
 * @author Hospital Management System
 * @version 1.0
 * @see Hospital
 * @see Patient
 */
public class AdmissionService {

    /** The hospital instance this service operates on. */
    private final Hospital hospital;

    /**
     * Constructs an {@code AdmissionService} bound to the given hospital.
     *
     * @param hospital the {@link Hospital} instance to manage; must not be {@code null}
     */
    public AdmissionService(Hospital hospital) {
        this.hospital = hospital;
    }

    /**
     * Registers a new patient in the hospital system.
     *
     * <p>Validates all input fields, creates a new {@link Patient} with status
     * {@code REGISTERED}, and adds the patient to the hospital registry.</p>
     *
     * @param name          the patient's full name; must not be blank
     * @param age           the patient's age; must be a positive integer within valid range
     * @param gender        the patient's {@link Gender}; must not be {@code null}
     * @param contactNumber the patient's contact number; must match expected format
     * @param address       the patient's residential address; must not be empty
     * @return the newly created and registered {@link Patient}
     * @throws HospitalException if any validation check fails
     */
    public Patient registerPatient(String name, int age, Gender gender,
                                   String contactNumber, String address) throws HospitalException {
        HospitalValidator.validateName(name);
        HospitalValidator.validateAge(age);
        HospitalValidator.validateContactNumber(contactNumber);
        HospitalValidator.validateNotEmpty(address, "Address");
        HospitalValidator.validateNotNull(gender, "Gender");

        Patient patient = new Patient(name, age, gender, contactNumber, address);
        hospital.addPatient(patient);
        return patient;
    }

    /**
     * Admits a previously registered patient.
     *
     * <p>Transitions the patient's status from {@code REGISTERED} to
     * {@code ADMITTED}. Throws if the patient is already admitted or in an
     * invalid state for admission.</p>
     *
     * @param patientId the unique identifier of the patient to admit
     * @throws PatientNotFoundException      if no patient with the given ID exists
     * @throws InvalidPatientStateException  if the patient is not in a valid state for admission
     * @throws AlreadyAdmittedException      if the patient is already admitted
     */
    public void admitPatient(String patientId)
            throws PatientNotFoundException, InvalidPatientStateException, AlreadyAdmittedException {
        Patient patient = hospital.getPatient(patientId);
        patient.admit();
    }

    /**
     * Starts treatment for an admitted patient.
     *
     * <p>Transitions the patient's status from {@code ADMITTED} to
     * {@code UNDER_TREATMENT}.</p>
     *
     * @param patientId the unique identifier of the patient
     * @throws PatientNotFoundException      if no patient with the given ID exists
     * @throws InvalidPatientStateException  if the patient is not in the {@code ADMITTED} state
     */
    public void startTreatment(String patientId)
            throws PatientNotFoundException, InvalidPatientStateException {
        Patient patient = hospital.getPatient(patientId);
        patient.startTreatment();
    }

    /**
     * Discharges a patient who is currently under treatment.
     *
     * <p>Before discharging, this method performs cleanup:
     * <ul>
     *   <li>Releases the occupied bed in the assigned ward (if any).</li>
     *   <li>Removes the patient from the assigned doctor's patient list (if any).</li>
     * </ul>
     * If the ward or doctor has already been removed from the system, the
     * corresponding cleanup step is silently skipped.</p>
     *
     * @param patientId the unique identifier of the patient to discharge
     * @throws PatientNotFoundException      if no patient with the given ID exists
     * @throws InvalidPatientStateException  if the patient is not in the {@code UNDER_TREATMENT} state
     * @throws HospitalException             if an unexpected hospital-level error occurs
     */
    public void dischargePatient(String patientId)
            throws PatientNotFoundException, InvalidPatientStateException, HospitalException {
        Patient patient = hospital.getPatient(patientId);

        // Release ward bed if assigned
        String wardId = patient.getAssignedWardId();
        if (wardId != null && !wardId.isEmpty()) {
            try {
                Ward ward = hospital.getWard(wardId);
                ward.releaseBed();
            } catch (HospitalException e) {
                // Ward may have been removed; log but continue discharge
            }
        }

        // Remove patient from doctor's list if assigned
        String doctorId = patient.getAssignedDoctorId();
        if (doctorId != null && !doctorId.isEmpty()) {
            try {
                Staff staff = hospital.getStaff(doctorId);
                if (staff instanceof Doctor) {
                    ((Doctor) staff).removePatient(patientId);
                }
            } catch (StaffAssignmentException e) {
                // Doctor may have been removed; continue
            }
        }

        patient.discharge();
    }

    /**
     * Assigns a doctor to a patient.
     *
     * <p>Validates that the specified staff member is indeed a {@link Doctor},
     * then establishes a bidirectional link: the patient records the doctor's
     * ID and the doctor adds the patient to its assigned-patient list.</p>
     *
     * @param patientId the unique identifier of the patient
     * @param doctorId  the unique identifier of the doctor (staff member)
     * @throws PatientNotFoundException      if no patient with the given ID exists
     * @throws StaffAssignmentException      if no staff with the given ID exists or the
     *                                       staff member is not a doctor
     * @throws InvalidPatientStateException  if the patient is not in a state that allows
     *                                       doctor assignment
     */
    public void assignDoctor(String patientId, String doctorId)
            throws PatientNotFoundException, StaffAssignmentException, InvalidPatientStateException {
        Patient patient = hospital.getPatient(patientId);
        Staff staff = hospital.getStaff(doctorId);

        if (!(staff instanceof Doctor)) {
            throw new StaffAssignmentException("Staff member '" + doctorId + "' is not a doctor.");
        }

        patient.assignDoctor(doctorId);
        ((Doctor) staff).assignPatient(patientId);
    }

    /**
     * Assigns a ward to a patient and occupies a bed in that ward.
     *
     * <p>Checks that the target ward has available beds before proceeding.
     * On success the patient records the ward assignment and the ward's
     * occupied-bed count is incremented.</p>
     *
     * @param patientId the unique identifier of the patient
     * @param wardId    the unique identifier of the ward
     * @throws PatientNotFoundException      if no patient with the given ID exists
     * @throws InvalidPatientStateException  if the patient is not in a state that allows
     *                                       ward assignment
     * @throws HospitalException             if the ward does not exist or has no available beds
     */
    public void assignWard(String patientId, String wardId)
            throws PatientNotFoundException, InvalidPatientStateException, HospitalException {
        Patient patient = hospital.getPatient(patientId);
        Ward ward = hospital.getWard(wardId);

        if (ward.isFull()) {
            throw new HospitalException("Ward '" + ward.getWardName() + "' has no available beds.");
        }

        patient.assignWard(wardId);
        ward.occupyBed();
    }

    /**
     * Retrieves a patient by their unique identifier.
     *
     * @param patientId the unique identifier of the patient
     * @return the {@link Patient} with the given ID
     * @throws PatientNotFoundException if no patient with the given ID exists
     */
    public Patient getPatient(String patientId) throws PatientNotFoundException {
        return hospital.getPatient(patientId);
    }
}
