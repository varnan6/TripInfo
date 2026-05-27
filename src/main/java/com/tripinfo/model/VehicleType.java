package com.tripinfo.model;

public enum VehicleType {

    // Accepting two types of vehicles. Restricted using ENUM type.
    SEDAN("Sedan"),
    SUV("SUV");

    // Name to display.
    private final String displayName;

    // Constructor accepting name to display
    VehicleType(String displayName) {
        this.displayName = displayName;
    }

    // Parameter getter methods
    public String getDisplayName() {
        return displayName;
    }

    // Function returning "VehicleType" object from given string by checking for a valid vehicle type.
    public static VehicleType fromString(String strRaw) {
        if (strRaw == null || strRaw.isBlank()) {
            throw new IllegalArgumentException("Vehicle type string is null or blank.");
        }
        for (VehicleType type : values()) {

            // Checks for valid vehicle type
            if (type.displayName.equalsIgnoreCase(strRaw.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown vehicle type: '" + strRaw.trim() + "'");
    }

    @Override
    public String toString() {
        return displayName;
    }
}
