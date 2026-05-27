package com.tripinfo.service;

import com.tripinfo.parser.ParseResult;
import com.tripinfo.parser.TripParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Runs once at startup (implements ApplicationRunner).
 * Reads the CSV file, parses it, and loads valid trips into TripStore.
 * Invalid records are logged to stdout. It has the same behaviour as the
 * console app.
 */

@Component
public class DataLoader implements ApplicationRunner {

    // Private attributes
    private final TripStore tripStore;
    private final TripParser parser;

    // Inject the data from .csv file to the string below
    @Value("${app.data.file:trips.csv}")
    private String dataFile;

    // Full parameterised constructor
    public DataLoader(TripStore tripStore, TripParser parser) {
        this.tripStore = tripStore;
        this.parser = parser;
    }

    // Runs the dataloader's function for reading the data file contexts.
    @Override
    public void run(ApplicationArguments args) {

        System.out.println("[DataLoader] Reading: " + dataFile);

        List<String> rawLines;
        try {
            rawLines = Files.readAllLines(Paths.get(dataFile));
        } catch (IOException e) {
            System.err.println("[DataLoader] WARNING: Could not read '" + dataFile + "'. "
                    + "API will start with an empty trip list. (" + e.getMessage() + ")");
            return;
        }

        // Using the parser object to parse the raw lines
        ParseResult result = parser.parse(rawLines);
        tripStore.addAll(result.getValidTrips()); // Adding all trips to the object storing all trips (TripStore)

        // Printing parsing information
        System.out.println("[DataLoader] Loaded " + result.getValidTrips().size()
                + " valid trips, " + result.getErrors().size() + " skipped.");

        // Printing all parsing errors
        result.getErrors().forEach(err -> System.out.println("[DataLoader] SKIP: " + err));
    }
}
