package com.hospital.util;

import com.hospital.exception.HospitalException;

/**
 * Centralized validation utility for hospital domain objects.
 * All methods are static and throw {@link HospitalException} on validation failure.
 */
public final class HospitalValidator {

    private HospitalValidator() {
        // Utility class
    }

    public static void validateName(String name) throws HospitalException {
        validateNotEmpty(name, "Name");
        if (name.length() < 2 || name.length() > 100) {
            throw new HospitalException("Name must be between 2 and 100 characters.");
        }
        if (!name.matches("[a-zA-Z .'-]+")) {
            throw new HospitalException("Name contains invalid characters. Only letters, spaces, hyphens, apostrophes, and dots are allowed.");
        }
    }

    public static void validateAge(int age) throws HospitalException {
        if (age < 0 || age > 150) {
            throw new HospitalException("Age must be between 0 and 150. Provided: " + age);
        }
    }

    public static void validateContactNumber(String contact) throws HospitalException {
        validateNotEmpty(contact, "Contact number");
        String digits = contact.replaceAll("[^0-9]", "");
        if (digits.length() < 10 || digits.length() > 15) {
            throw new HospitalException("Contact number must contain 10–15 digits. Provided: " + contact);
        }
    }

    public static void validateNotNull(Object obj, String fieldName) throws HospitalException {
        if (obj == null) {
            throw new HospitalException(fieldName + " must not be null.");
        }
    }

    public static void validateNotEmpty(String str, String fieldName) throws HospitalException {
        if (str == null || str.trim().isEmpty()) {
            throw new HospitalException(fieldName + " must not be null or empty.");
        }
    }
}
