package com.hospital.enums;

/**
 * Represents the lifecycle status of a patient within the hospital system.
 * Enforces valid state transitions:
 * REGISTERED → ADMITTED → UNDER_TREATMENT → DISCHARGED
 */
public enum PatientStatus {
    REGISTERED("Registered"),
    ADMITTED("Admitted"),
    UNDER_TREATMENT("Under Treatment"),
    DISCHARGED("Discharged");

    private final String displayName;

    PatientStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Validates whether a transition from this status to the target status is allowed.
     * @param target the desired next status
     * @return true if the transition is valid
     */
    public boolean canTransitionTo(PatientStatus target) {
        switch (this) {
            case REGISTERED:
                return target == ADMITTED;
            case ADMITTED:
                return target == UNDER_TREATMENT;
            case UNDER_TREATMENT:
                return target == DISCHARGED;
            case DISCHARGED:
                return false;
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return displayName;
    }
}
