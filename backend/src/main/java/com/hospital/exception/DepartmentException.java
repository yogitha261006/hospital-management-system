package com.hospital.exception;

/**
 * Thrown when a department operation fails (e.g., department not found,
 * invalid assignment, capacity exceeded).
 */
public class DepartmentException extends HospitalException {

    public DepartmentException(String message) {
        super(message);
    }

    public DepartmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
