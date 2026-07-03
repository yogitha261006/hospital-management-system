package com.hospital.exception;

/**
 * Thrown when attempting to admit a patient who is already in an admitted state.
 */
public class AlreadyAdmittedException extends InvalidPatientStateException {

    public AlreadyAdmittedException(String patientId) {
        super("Patient '" + patientId + "' is already admitted and cannot be admitted again.");
    }

    public AlreadyAdmittedException(String patientId, Throwable cause) {
        super("Patient '" + patientId + "' is already admitted and cannot be admitted again.", cause);
    }
}
