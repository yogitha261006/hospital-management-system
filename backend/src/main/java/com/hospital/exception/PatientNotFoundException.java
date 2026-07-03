package com.hospital.exception;

/**
 * Thrown when a patient lookup fails to find a matching record.
 */
public class PatientNotFoundException extends HospitalException {

    public PatientNotFoundException(String patientId) {
        super("Patient not found with ID: " + patientId);
    }

    public PatientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
