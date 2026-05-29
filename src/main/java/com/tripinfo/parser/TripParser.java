package com.tripinfo.parser;

import com.tripinfo.model.Trip;
import com.tripinfo.model.VehicleType;

import java.util.ArrayList;
import java.util.List;

public class TripParser {

    // Limiting rating range and driver ID length for *assumed* security reasons
    private static final int EXPECTED_FIELD_COUNT = 5;
    private static final double MIN_RATING = 1.0;
    private static final double MAX_RATING = 5.0;
    private static final int MAX_DRIVER_ID_LEN = 20; // security: cap field length

    // Parsing raw lines extracted from .csv file
    public ParseResult parse(List<String> rawLines) {
        List<Trip> validTrips = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // Checking for no records
        if (rawLines == null || rawLines.isEmpty()) {
            errors.add("Input data is empty. No records to process.");
            return new ParseResult(validTrips, errors);
        }

        // tracking number of lines
        int lineNumber = 0;

        // Iterating over raw lines
        for (String line : rawLines) {
            lineNumber++;

            // Checking for a blank line
            if (line == null || line.isBlank())
                continue;

            // if the starting word is "driverid", it's the title line, ignore it, and move
            // on to the next line
            if (line.trim().toLowerCase().startsWith("driverid"))
                continue;

            //
            try {
                validTrips.add(parseLine(line.trim(), lineNumber));
            } catch (TripParseException e) {
                errors.add(e.getMessage());
            }
        }
        return new ParseResult(validTrips, errors);
    }

    // Line parsing method. Returns Trip object at success.
    private Trip parseLine(String line, int lineNumber) throws TripParseException {

        // Splitting each line to get value for each field
        String[] fields = line.split(",", -1);

        // If line number is 0, then the body of the content hasn't arrived.
        String loc = lineNumber > 0 ? "Line " + lineNumber : "Request body";

        // If the number of fields doesn't match the expected count, raise error.
        if (fields.length != EXPECTED_FIELD_COUNT) {
            throw new TripParseException(loc + ": Expected " + EXPECTED_FIELD_COUNT
                    + " fields but found " + fields.length + ". Raw: [" + line + "]");
        }

        // Assign values to temporary variables for each field.
        String driverId = fields[0].trim();
        String vehicleRaw = fields[1].trim();
        String distanceRaw = fields[2].trim();
        String fareRaw = fields[3].trim();
        String ratingRaw = fields[4].trim();

        // Handling invalid driver ID cases
        if (driverId.isEmpty())
            throw new TripParseException(loc + ": DriverID is blank.");
        if (driverId.length() > MAX_DRIVER_ID_LEN)
            throw new TripParseException(loc + ": DriverID exceeds max length of " + MAX_DRIVER_ID_LEN + ".");

        // Handling invalid vehicle type cases
        VehicleType vehicleType;
        try {
            vehicleType = VehicleType.fromString(vehicleRaw);
        } catch (IllegalArgumentException e) {
            throw new TripParseException(loc + ": Invalid vehicle type '" + vehicleRaw + "'. Accepted: SEDAN, SUV.");
        }

        // getting non-negative confirmed trip distance and paid fare values
        double tripDistanceKm = parseNonNegativeDouble(distanceRaw, "Trip distance", loc);
        double farePaid = parseNonNegativeDouble(fareRaw, "Fare", loc);

        // parsing and storying valid float (not double, it's overkill) customer rating.
        double customerRating;
        try {
            customerRating = Double.parseDouble(ratingRaw);
        } catch (NumberFormatException e) {
            throw new TripParseException(loc + ": Customer rating '" + ratingRaw + "' is not a valid number.");
        }
        if (customerRating < MIN_RATING || customerRating > MAX_RATING) {
            throw new TripParseException(loc + ": Customer rating " + customerRating
                    + " is outside valid range [1.0, 5.0].");
        }

        // returning Trip object on successful parsing
        return new Trip(driverId, vehicleType, tripDistanceKm, farePaid, customerRating);
    }

    // Checking for valid, non-negative double values from string
    private double parseNonNegativeDouble(String raw, String fieldName, String loc) throws TripParseException {

        double value;
        try {
            value = Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            throw new TripParseException(loc + ": " + fieldName + " '" + raw + "' is not a valid number.");
        }
        if (value < 0.0)
            throw new TripParseException(loc + ": " + fieldName + " cannot be negative (" + value + ").");
        return value;
    }
}
