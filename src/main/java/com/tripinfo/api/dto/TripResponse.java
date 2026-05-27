package com.tripinfo.api.dto;

import com.tripinfo.model.Trip;

/**
 * Response DTO for a single Trip.
 * Converts the domain object into a flat, JSON-friendly shape.
 */

public class TripResponse {

    private String driverId;
    private String vehicleType;
    private double tripDistanceKm;
    private double farePaid;
    private double customerRating;

    public TripResponse() {}

    // Protected constructor so subclasses can populate all base fields in one call
    protected TripResponse(Trip t) {
        this.driverId       = t.getDriverId();
        this.vehicleType    = t.getVehicleType().getDisplayName();
        this.tripDistanceKm = t.getTripDistanceKm();
        this.farePaid       = t.getFarePaid();
        this.customerRating = t.getCustomerRating();
    }

    public static TripResponse from(Trip t) {
        return new TripResponse(t);
    }

    public String getDriverId()       { return driverId; }
    public String getVehicleType()    { return vehicleType; }
    public double getTripDistanceKm() { return tripDistanceKm; }
    public double getFarePaid()       { return farePaid; }
    public double getCustomerRating() { return customerRating; }
}