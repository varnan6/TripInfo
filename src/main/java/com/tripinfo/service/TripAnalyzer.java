package com.tripinfo.service;

import com.tripinfo.model.Trip;

import java.util.*;
import java.util.stream.Collectors;

public class TripAnalyzer {

        // Setting a lower limit for performance rating.
        private static final double UNDERPERFORMING_THRESHOLD = 3.5;

        // Averaging each driver's ratings
        public List<DriverSummary> averageRatingByDriver(List<Trip> trips) {

                Map<String, Double> avgMap = trips.stream()
                                .collect(Collectors.groupingBy(Trip::getDriverId,
                                                Collectors.averagingDouble(Trip::getCustomerRating)));

                Map<String, Long> countMap = trips.stream()
                                .collect(Collectors.groupingBy(Trip::getDriverId, Collectors.counting()));

                return avgMap.entrySet().stream()
                                .map(e -> new DriverSummary(e.getKey(), e.getValue(),
                                                countMap.get(e.getKey()).intValue()))
                                .sorted(Comparator.comparingDouble(DriverSummary::getAverageRating).reversed())
                                .collect(Collectors.toList());
        }

        // Finding the most efficient trip (highest earning/km ratio)
        public Optional<Trip> findMostEfficientTrip(List<Trip> trips) {
                return trips.stream()
                                .filter(t -> t.getTripDistanceKm() > 0.0)
                                .max(Comparator.comparingDouble(Trip::getEarningsPerKm));
        }

        // Getting drivers with an average rating of below UNDERPERFORMING_THRESHOLD
        // value
        public List<DriverSummary> findUnderperformingDrivers(List<Trip> trips) {
                return averageRatingByDriver(trips).stream()
                                .filter(ds -> ds.getAverageRating() < UNDERPERFORMING_THRESHOLD)
                                .sorted(Comparator.comparingDouble(DriverSummary::getAverageRating))
                                .collect(Collectors.toList());
        }

        // Finding total fare charged of each *vehicle type*, instead of driver.
        public Map<String, Double> totalFareByVehicleType(List<Trip> trips) {
                return trips.stream()
                                .collect(Collectors.groupingBy(t -> t.getVehicleType().getDisplayName(),
                                                Collectors.summingDouble(Trip::getFarePaid)))
                                .entrySet().stream()
                                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                                (a, b) -> a, LinkedHashMap::new));
        }
}
