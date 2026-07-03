package com.hospital.management.exception;

public class StaffUnavailableException extends RuntimeException {
    public StaffUnavailableException(String message) {
        super(message);
    }
}
