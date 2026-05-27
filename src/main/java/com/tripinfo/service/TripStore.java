package com.tripinfo.service;

import com.tripinfo.model.Trip;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe in-memory store for all valid Trip records.
 *
 * CopyOnWriteArrayList is used so that read-heavy analytical endpoints
 * never block each other, and POST /api/trips writes are still safe.
 */

@Component
public class TripStore {

    // A thread-safe way of using an arraylist. Creates a new copy of the internal array on any mutation-kind of operation.
    private final CopyOnWriteArrayList<Trip> trips = new CopyOnWriteArrayList<>();

    // Adding a list of trips to the current list
    public void addAll(List<Trip> incoming) {
        trips.addAll(incoming);
    }

    // Adding a single trip to the list of trips.
    public void add(Trip trip) {
        trips.add(trip);
    }

    /** Returns a snapshot of the current trip list (safe for iteration). */
    public List<Trip> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(trips));
    }

    // Getting the size of the number of trips
    public int size() {
        return trips.size();
    }
}
