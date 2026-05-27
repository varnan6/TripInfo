package com.tripinfo.api.controller;

import com.tripinfo.api.dto.TripRequest;
import com.tripinfo.api.dto.TripResponse;
import com.tripinfo.model.Trip;
import com.tripinfo.model.VehicleType;
import com.tripinfo.service.TripStore;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles trip-level CRUD endpoints.
 *
 * GET /api/trips - returns all valid trips loaded from the CSV
 * POST /api/trips - validates and adds a new trip to the in-memory store
 *
 * Controllers are kept thin: no business logic lives here.
 * All domain objects are converted to/from DTOs at this layer.
 */
@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripStore tripStore;

    public TripController(TripStore tripStore) {
        this.tripStore = tripStore;
    }

    /**
     * GET /api/trips
     * Returns all currently loaded trip records.
     */
    @GetMapping
    public ResponseEntity<List<TripResponse>> getAllTrips() {
        List<TripResponse> response = tripStore.getAll()
                .stream()
                .map(TripResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/trips
     * Accepts a JSON trip body, validates it via Bean Validation (@Valid),
     * converts it to a domain Trip, and appends it to the store.
     *
     * Any validation failure is handled by GlobalExceptionHandler → 400.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> addTrip(@Valid @RequestBody TripRequest request) {

        // VehicleType conversion — gives a clear 400 if the string is unknown
        VehicleType vehicleType;
        try {
            vehicleType = VehicleType.fromString(request.getVehicleType());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid vehicleType: '"
                            + request.getVehicleType() + "'. Accepted values: Sedan, SUV."));
        }

        Trip trip = new Trip(
                request.getDriverId(),
                vehicleType,
                request.getTripDistanceKm(),
                request.getFarePaid(),
                request.getCustomerRating());

        tripStore.add(trip);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Trip added successfully"));
    }
}
