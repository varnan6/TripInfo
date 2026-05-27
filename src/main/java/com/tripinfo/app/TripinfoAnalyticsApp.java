package com.tripinfo.app;

import com.tripinfo.parser.ParseResult;
import com.tripinfo.parser.TripParser;
import com.tripinfo.service.DriverSummary;
import com.tripinfo.service.TripAnalyzer;
import com.tripinfo.model.Trip;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * Console entry point for the Ride-Hailing Driver Performance Analytics Tool.
 *
 * Reads trips.csv (or a path passed as the first argument), parses the records,
 * runs all analytics, and prints the results to stdout.
 *
 * Run:
 *   javac -d out src/**{@literal /}*.java
 *   java -cp out com.tripinfo.app.TripInfoAnalyticsApp
 * Or with a custom data file:
 *   java -cp out com.tripinfo.app.TripInfoAnalyticsApp path/to/trips.csv
 */
public class TripInfoAnalyticsApp {

    public static void main(String[] args) {

        // Allow overriding the data file via command-line argument
        String dataFile = (args.length > 0) ? args[0] : "trips.csv";

        // ── 1. Load raw lines ────────────────────────────────────────────────
        List<String> rawLines;
        try {
            rawLines = Files.readAllLines(Paths.get(dataFile));
        } catch (IOException e) {
            System.err.println("ERROR: Could not read '" + dataFile + "': " + e.getMessage());
            System.exit(1);
            return; // keeps the compiler happy
        }

        // ── 2. Parse ─────────────────────────────────────────────────────────
        TripParser parser = new TripParser();
        ParseResult result = parser.parse(rawLines);

        List<Trip> trips = result.getValidTrips();

        // ── 3. Print header ───────────────────────────────────────────────────
        System.out.println("==========================================");
        System.out.println(" Ride-Hailing Driver Performance Analytics");
        System.out.println("==========================================");
        System.out.println("Total Valid Trips  : " + trips.size());
        System.out.println("Total Invalid Trips: " + result.getErrors().size());

        // Print skipped-record warnings, if any
        if (result.hasErrors()) {
            System.out.println("\nSkipped Records (invalid data):");
            result.getErrors().forEach(err -> System.out.println("  [SKIP] " + err));
        }

        // ── 4. Analytics ──────────────────────────────────────────────────────
        TripAnalyzer analyzer = new TripAnalyzer();

        // 4a. Average rating by driver (descending)
        System.out.println("\nAverage Rating by Driver:");
        List<DriverSummary> ratings = analyzer.averageRatingByDriver(trips);
        if (ratings.isEmpty()) {
            System.out.println("  No trips available.");
        } else {
            ratings.forEach(ds ->
                System.out.printf("  %-8s -> %.2f%n", ds.getDriverId(), ds.getAverageRating()));
        }

        // 4b. Most efficient trip (highest fare/km, zero-distance excluded)
        System.out.println("\nMost Efficient Trip:");
        Optional<Trip> best = analyzer.findMostEfficientTrip(trips);
        if (!best.isPresent()) {
            System.out.println("  No qualifying trips found (all trips may have zero distance).");
        } else {
            Trip t = best.get();
            System.out.println("  Driver  : " + t.getDriverId());
            System.out.println("  Vehicle : " + t.getVehicleType().getDisplayName());
            System.out.printf( "  Distance: %.1f km%n", t.getTripDistanceKm());
            System.out.printf( "  Fare    : %.2f%n",    t.getFarePaid());
            System.out.printf( "  Earnings Per Km: %.2f%n", t.getEarningsPerKm());
        }

        // 4c. Underperforming drivers (avg rating < 3.5, ascending)
        System.out.println("\nUnderperforming Drivers (avg rating < 3.5):");
        List<DriverSummary> underperforming = analyzer.findUnderperformingDrivers(trips);
        if (underperforming.isEmpty()) {
            System.out.println("  No underperforming drivers found.");
        } else {
            underperforming.forEach(ds ->
                System.out.printf("  %-8s -> Average Rating: %.2f%n",
                        ds.getDriverId(), ds.getAverageRating()));
        }

        System.out.println("\n==========================================");
    }
}