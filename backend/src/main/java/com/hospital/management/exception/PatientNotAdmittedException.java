package com.hospital.management.exception;

public class PatientNotAdmittedException extends RuntimeException {
    public PatientNotAdmittedException(String message) {
        super(message);
    }
}
