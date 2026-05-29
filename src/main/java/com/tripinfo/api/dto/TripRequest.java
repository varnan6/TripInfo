package com.tripinfo.api.dto;

import jakarta.validation.constraints.*;

/**
 * Request DTO for POST /api/trips.
 * Bean Validation annotations are used so Spring rejects malformed
 * requests before they reach service code — no manual null-checks needed.
 */
public class TripRequest {

    /* Adding necessary restrictions to attributes */

    @NotBlank(message = "driverId must not be blank")
    @Size(max = 20, message = "driverId must not exceed 20 characters")
    private String driverId;

    @NotBlank(message = "vehicleType must not be blank")
    private String vehicleType;

    @NotNull(message = "tripDistanceKm is required")
    @DecimalMin(value = "0.0", message = "tripDistanceKm must be >= 0")
    private Double tripDistanceKm;

    @NotNull(message = "farePaid is required")
    @DecimalMin(value = "0.0", message = "farePaid must be >= 0")
    private Double farePaid;

    @NotNull(message = "customerRating is required")
    @DecimalMin(value = "1.0", message = "customerRating must be >= 1.0")
    @DecimalMax(value = "5.0", message = "customerRating must be <= 5.0")
    private Double customerRating;

    // Getters & setters (no Lombok as stated in assignment)
    public String getDriverId() {
        return driverId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public Double getTripDistanceKm() {
        return tripDistanceKm;
    }

    public Double getFarePaid() {
        return farePaid;
    }

    public Double getCustomerRating() {
        return customerRating;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setTripDistanceKm(Double tripDistanceKm) {
        this.tripDistanceKm = tripDistanceKm;
    }

    public void setFarePaid(Double farePaid) {
        this.farePaid = farePaid;
    }

    public void setCustomerRating(Double customerRating) {
        this.customerRating = customerRating;
    }
}
