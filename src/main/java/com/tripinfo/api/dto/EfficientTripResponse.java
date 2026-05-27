package com.tripinfo.api.dto;

import com.tripinfo.model.Trip;

public class EfficientTripResponse extends TripResponse {

    private double earningsPerKm;

    public EfficientTripResponse() {
    }

    // Calls parent protected constructor for the 5 shared fields,
    // then sets the one field this subclass owns
    private EfficientTripResponse(Trip t) {
        super(t);
        this.earningsPerKm = t.getEarningsPerKm();
    }

    public static EfficientTripResponse from(Trip t) {
        return new EfficientTripResponse(t);
    }

    public double getEarningsPerKm() {
        return earningsPerKm;
    }
}