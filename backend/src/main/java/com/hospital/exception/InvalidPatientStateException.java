package com.hospital.exception;

/**
 * Thrown when an operation is attempted on a patient whose current
 * status does not permit the requested state transition.
 */
public class InvalidPatientStateException extends HospitalException {

    public InvalidPatientStateException(String message) {
        super(message);
    }

    public InvalidPatientStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
