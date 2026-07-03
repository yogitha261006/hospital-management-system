package com.hospital.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Immutable audit log entry recording a timestamped action performed on a domain entity.
 * Once created, an AuditEntry cannot be modified.
 *
 * <p>Each entry captures the exact moment an action occurred, a short action label
 * (e.g. {@code "PATIENT_REGISTERED"}), and a human-readable description of the event.</p>
 *
 * @author Hospital Management System
 * @version 1.0
 */
public final class AuditEntry {

    /** Formatter used for consistent timestamp rendering in {@link #toString()}. */
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** The exact date and time the action was recorded. */
    private final LocalDateTime timestamp;

    /** A short, uppercase label describing the action (e.g. "PATIENT_ADMITTED"). */
    private final String action;

    /** A human-readable description providing additional context about the action. */
    private final String details;

    /**
     * Creates a new audit entry with the current timestamp.
     *
     * @param action  short action label (e.g. "PATIENT_REGISTERED"); must not be {@code null}
     * @param details human-readable description of the event; must not be {@code null}
     */
    public AuditEntry(String action, String details) {
        this.timestamp = LocalDateTime.now();
        this.action = action;
        this.details = details;
    }

    /**
     * Returns the timestamp when this audit entry was created.
     *
     * @return the timestamp of this entry; never {@code null}
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the short action label for this audit entry.
     *
     * @return the action label; never {@code null}
     */
    public String getAction() {
        return action;
    }

    /**
     * Returns the human-readable details of this audit entry.
     *
     * @return the details string; never {@code null}
     */
    public String getDetails() {
        return details;
    }

    /**
     * Returns a formatted string representation of this audit entry.
     * Format: {@code [yyyy-MM-dd HH:mm:ss] ACTION_LABEL             — description}
     *
     * @return formatted audit entry string
     */
    @Override
    public String toString() {
        return String.format("[%s] %-25s — %s",
                timestamp.format(FORMATTER), action, details);
    }
}
