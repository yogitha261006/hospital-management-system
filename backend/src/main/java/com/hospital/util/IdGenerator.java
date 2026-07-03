package com.hospital.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe unique ID generator for all hospital domain entities.
 * Generates human-readable, prefixed identifiers.
 */
public final class IdGenerator {

    private static final AtomicInteger patientCounter = new AtomicInteger(0);
    private static final AtomicInteger staffCounter = new AtomicInteger(0);
    private static final AtomicInteger departmentCounter = new AtomicInteger(0);
    private static final AtomicInteger wardCounter = new AtomicInteger(0);
    private static final AtomicInteger mrnCounter = new AtomicInteger(0);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private IdGenerator() {
        // Utility class — prevent instantiation
    }

    public static String generatePatientId() {
        return String.format("PAT-%04d", patientCounter.incrementAndGet());
    }

    public static String generateStaffId() {
        return String.format("STF-%04d", staffCounter.incrementAndGet());
    }

    public static String generateDepartmentId() {
        return String.format("DEP-%04d", departmentCounter.incrementAndGet());
    }

    public static String generateWardId() {
        return String.format("WRD-%04d", wardCounter.incrementAndGet());
    }

    public static String generateMRN() {
        String datePart = LocalDate.now().format(DATE_FORMAT);
        return String.format("MRN-%s-%04d", datePart, mrnCounter.incrementAndGet());
    }

    /** Resets all counters. Intended for testing only. */
    public static void resetAll() {
        patientCounter.set(0);
        staffCounter.set(0);
        departmentCounter.set(0);
        wardCounter.set(0);
        mrnCounter.set(0);
    }
}
