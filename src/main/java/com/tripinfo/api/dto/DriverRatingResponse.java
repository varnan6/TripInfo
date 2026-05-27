package com.tripinfo.api.dto;

import com.tripinfo.service.DriverSummary;

public class DriverRatingResponse {

    private String driverId;
    private double averageRating;

    public DriverRatingResponse() {}

    public static DriverRatingResponse from(DriverSummary ds) {
        DriverRatingResponse r = new DriverRatingResponse();
        r.driverId     = ds.getDriverId();
        r.averageRating = ds.getAverageRating();
        return r;
    }

    public String getDriverId()       { return driverId; }
    public double getAverageRating()  { return averageRating; }
}
