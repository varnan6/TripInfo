package com.tripinfo.service;

// Creating driver's summary storing object
public class DriverSummary {

    // Private attributes
    private final String driverId;
    private final double averageRating;
    private final int    tripCount;

    // Full parameterised constructor
    public DriverSummary(String driverId, double averageRating, int tripCount) {
        this.driverId      = driverId;
        this.averageRating = averageRating;
        this.tripCount     = tripCount;
    }

    // An empty constructor will allow empty objects, which aren't valid according to the statement.

    // Attribute getter methods
    public String getDriverId()      { return driverId; }
    public double getAverageRating() { return averageRating; }
    public int    getTripCount()     { return tripCount; }
}
