package com.tripinfo.api.controller;

import com.tripinfo.api.dto.DriverRatingResponse;
import com.tripinfo.api.dto.EfficientTripResponse;
import com.tripinfo.api.dto.SummaryResponse;
import com.tripinfo.model.Trip;
import com.tripinfo.service.DriverSummary;
import com.tripinfo.service.TripAnalyzer;
import com.tripinfo.service.TripStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * All read-only analytics endpoints.
 *
 * GET /api/analytics/driver-ratings         - avg rating per driver, desc
 * GET /api/analytics/most-efficient-trip    - highest fare/km trip
 * GET /api/analytics/underperforming-drivers - avg rating < 3.5, asc
 * GET /api/analytics/summary                - combined report
 *
 * Every endpoint fetches a fresh snapshot from TripStore so that trips
 * added via POST /api/trips are immediately reflected.
 */


@RestController
@RequestMapping("/api/analytics") // base URL path for analytics endpoints.
public class AnalyticsController {

    // trip store and analyser object attributes.
    private final TripStore    tripStore;
    private final TripAnalyzer analyzer;

    // Fully parameterised constructors.
    public AnalyticsController(TripStore tripStore, TripAnalyzer analyzer) {
        this.tripStore = tripStore;
        this.analyzer  = analyzer;
    }

    /**
     * GET /api/analytics/driver-ratings
     * Endpoint to get average rating per driver (in descending order).
     */

    @GetMapping("/driver-ratings")
    public ResponseEntity<List<DriverRatingResponse>> driverRatings() {
        List<DriverRatingResponse> result = analyzer
                .averageRatingByDriver(tripStore.getAll())
                .stream()
                .map(DriverRatingResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/analytics/most-efficient-trip
     * The trip with the highest earnings per km (zero-distance trips excluded).
     * Returns 404 if no qualifying trip exists.
     */

    @GetMapping("/most-efficient-trip")
    public ResponseEntity<?> mostEfficientTrip() {
        Optional<Trip> best = analyzer.findMostEfficientTrip(tripStore.getAll());

        if (!best.isPresent()) {
            return ResponseEntity.status(404)
                    .body(Map.of("message",
                            "No qualifying trips found (all trips may have zero distance)."));
        }

        return ResponseEntity.ok(EfficientTripResponse.from(best.get()));
    }

    /**
     * GET /api/analytics/underperforming-drivers
     * Drivers with average rating below 3.5, sorted ascending.
     */
    @GetMapping("/underperforming-drivers")
    public ResponseEntity<List<DriverRatingResponse>> underperformingDrivers() {
        List<DriverRatingResponse> result = analyzer
                .findUnderperformingDrivers(tripStore.getAll())
                .stream()
                .map(DriverRatingResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/analytics/summary
     * Combined report: trip counts, driver ratings, most efficient trip,
     * and underperforming drivers in one response.
     *
     * totalInvalidTrips is not tracked at runtime (trips added via POST are
     * already validated), so it is reported as 0 for live additions.
     * The initial CSV load count is printed to the console by DataLoader.
     */
    @GetMapping("/summary")
    public ResponseEntity<SummaryResponse> summary() {
        List<Trip> trips = tripStore.getAll();

        List<DriverRatingResponse> driverRatings = analyzer
                .averageRatingByDriver(trips)
                .stream()
                .map(DriverRatingResponse::from)
                .collect(Collectors.toList());

        Optional<Trip> best = analyzer.findMostEfficientTrip(trips);

        List<DriverRatingResponse> underperforming = analyzer
                .findUnderperformingDrivers(trips)
                .stream()
                .map(DriverRatingResponse::from)
                .collect(Collectors.toList());

        SummaryResponse summary = new SummaryResponse();
        summary.setTotalValidTrips(trips.size());
        summary.setTotalInvalidTrips(0);   // see Javadoc above
        summary.setDriverRatings(driverRatings);
        summary.setMostEfficientTrip(best.map(EfficientTripResponse::from).orElse(null));
        summary.setUnderperformingDrivers(underperforming);

        return ResponseEntity.ok(summary);
    }
}
