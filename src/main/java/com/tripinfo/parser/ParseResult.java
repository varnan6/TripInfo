package com.tripinfo.parser;

import com.tripinfo.model.Trip;

import java.util.Collections;
import java.util.List;

// Stores parsing results in a pair of lists
public class ParseResult {

    private final List<Trip> validTrips; // List of valid Trip objects
    private final List<String> errors; // List of errors (in String format)

    // Full parameterised constructor
    public ParseResult(List<Trip> validTrips, List<String> errors) {
        this.validTrips = Collections.unmodifiableList(validTrips);
        this.errors = Collections.unmodifiableList(errors);
    }

    // List attribute getter methods
    public List<Trip> getValidTrips() {
        return validTrips;
    }

    public List<String> getErrors() {
        return errors;
    }

    // Checks whether any errors exist
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
