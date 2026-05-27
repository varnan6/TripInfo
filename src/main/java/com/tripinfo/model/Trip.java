package com.tripinfo.model;

public class Trip {

    // Parameters (private) to save for each trip object.
    private final String driverId;
    private final VehicleType vehicleType;
    private final double tripDistanceKm;
    private final double farePaid;
    private final double customerRating;

    // Full parameterised constructor
    public Trip(String driverId, VehicleType vehicleType,
            double tripDistanceKm, double farePaid, Double customerRating) {
        this.driverId = driverId;
        this.vehicleType = vehicleType;
        this.tripDistanceKm = tripDistanceKm;
        this.farePaid = farePaid;
        this.customerRating = customerRating;
    }

    // We will not entertain an empty/default constructor, as that will embody an
    // invalid trip.

    // Parameter getter methods
    public String getDriverId() {
        return driverId;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public double getTripDistanceKm() {
        return tripDistanceKm;
    }

    public double getFarePaid() {
        return farePaid;
    }

    public double getCustomerRating() {
        return customerRating;
    }

    public double getEarningsPerKm() {
        return (tripDistanceKm == 0.0) ? 0.0 : farePaid / tripDistanceKm;
        // Handled edge case where the trip distance is 0 (divide by zero error)
    }
}
