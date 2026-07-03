package com.hospital.exception;

/**
 * Base exception for all hospital domain exceptions.
 * Provides a common ancestor for structured exception handling.
 */
public class HospitalException extends Exception {

    public HospitalException(String message) {
        super(message);
    }

    public HospitalException(String message, Throwable cause) {
        super(message, cause);
    }
}
