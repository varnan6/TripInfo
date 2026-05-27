package com.tripinfo.api.dto;

import java.util.List;

public class SummaryResponse {

    private int totalValidTrips;
    private int totalInvalidTrips;
    private List<DriverRatingResponse>  driverRatings;
    private EfficientTripResponse       mostEfficientTrip;
    private List<DriverRatingResponse>  underperformingDrivers;

    public SummaryResponse() {}

    public int getTotalValidTrips()                              { return totalValidTrips; }
    public int getTotalInvalidTrips()                            { return totalInvalidTrips; }
    public List<DriverRatingResponse>  getDriverRatings()        { return driverRatings; }
    public EfficientTripResponse       getMostEfficientTrip()    { return mostEfficientTrip; }
    public List<DriverRatingResponse>  getUnderperformingDrivers(){ return underperformingDrivers; }

    public void setTotalValidTrips(int v)                                    { this.totalValidTrips = v; }
    public void setTotalInvalidTrips(int v)                                  { this.totalInvalidTrips = v; }
    public void setDriverRatings(List<DriverRatingResponse> v)               { this.driverRatings = v; }
    public void setMostEfficientTrip(EfficientTripResponse v)                { this.mostEfficientTrip = v; }
    public void setUnderperformingDrivers(List<DriverRatingResponse> v)      { this.underperformingDrivers = v; }
}
