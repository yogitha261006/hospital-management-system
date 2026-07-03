package com.hospital.model;

import com.hospital.exception.HospitalException;
import com.hospital.util.IdGenerator;

/**
 * Represents a hospital ward with bed capacity tracking.
 *
 * <p>Each ward has a fixed total number of beds and tracks current occupancy.
 * Beds can be occupied or released, with validation to prevent over-occupancy
 * or negative occupancy counts.</p>
 *
 * @author Hospital Management System
 * @version 1.0
 */
public class Ward {

    /** Unique identifier for this ward, auto-generated on creation. */
    private final String wardId;

    /** Human-readable name of the ward (e.g. "ICU", "General Ward A"). */
    private String wardName;

    /** Total number of beds available in this ward (immutable after creation). */
    private final int totalBeds;

    /** Current number of occupied beds. */
    private int occupiedBeds;

    /**
     * Creates a new ward with the specified name and total bed capacity.
     *
     * <p>The ward ID is automatically generated via {@link IdGenerator#generateWardId()}.
     * Occupied beds are initialized to zero.</p>
     *
     * @param wardName  the name of the ward; must not be {@code null} or empty
     * @param totalBeds the total number of beds; must be greater than zero
     * @throws IllegalArgumentException if wardName is null/empty or totalBeds is not positive
     */
    public Ward(String wardName, int totalBeds) {
        if (wardName == null || wardName.trim().isEmpty()) {
            throw new IllegalArgumentException("Ward name cannot be null or empty");
        }
        if (totalBeds <= 0) {
            throw new IllegalArgumentException("Total beds must be greater than zero");
        }
        this.wardId = IdGenerator.generateWardId();
        this.wardName = wardName;
        this.totalBeds = totalBeds;
        this.occupiedBeds = 0;
    }

    /**
     * Returns the unique identifier for this ward.
     *
     * @return the ward ID; never {@code null}
     */
    public String getWardId() {
        return wardId;
    }

    /**
     * Returns the human-readable name of this ward.
     *
     * @return the ward name; never {@code null}
     */
    public String getWardName() {
        return wardName;
    }

    /**
     * Returns the total number of beds in this ward.
     *
     * @return the total bed count
     */
    public int getTotalBeds() {
        return totalBeds;
    }

    /**
     * Returns the number of currently occupied beds.
     *
     * @return the occupied bed count
     */
    public int getOccupiedBeds() {
        return occupiedBeds;
    }

    /**
     * Returns the number of available (unoccupied) beds.
     *
     * @return {@code totalBeds - occupiedBeds}
     */
    public int getAvailableBeds() {
        return totalBeds - occupiedBeds;
    }

    /**
     * Checks whether this ward is at full capacity.
     *
     * @return {@code true} if occupied beds equals or exceeds total beds
     */
    public boolean isFull() {
        return occupiedBeds >= totalBeds;
    }

    /**
     * Occupies one bed in this ward.
     *
     * @throws HospitalException if the ward is already at full capacity
     */
    public void occupyBed() throws HospitalException {
        if (isFull()) {
            throw new HospitalException(
                    "Ward '" + wardName + "' is full. No available beds.");
        }
        occupiedBeds++;
    }

    /**
     * Releases one bed in this ward.
     *
     * @throws HospitalException if there are no occupied beds to release
     */
    public void releaseBed() throws HospitalException {
        if (occupiedBeds <= 0) {
            throw new HospitalException(
                    "Ward '" + wardName + "' has no occupied beds to release.");
        }
        occupiedBeds--;
    }

    /**
     * Returns a formatted string representation of this ward.
     *
     * @return a string containing ward name, total beds, and available beds
     */
    @Override
    public String toString() {
        return String.format("Ward[id=%s, name='%s', totalBeds=%d, availableBeds=%d]",
                wardId, wardName, totalBeds, getAvailableBeds());
    }
}
