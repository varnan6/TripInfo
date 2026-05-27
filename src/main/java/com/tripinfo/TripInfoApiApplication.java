package com.tripinfo;

import com.tripinfo.parser.TripParser;
import com.tripinfo.service.TripAnalyzer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot entry point for the Ride-Hailing Analytics REST API.
 *
 * The console application (tripinfoAnalyticsApp) remains fully independent.
 * This class starts the embedded Tomcat server and wires the Spring context.
 *
 * On startup, DataLoader reads trips.csv and populates TripStore automatically.
 */
@SpringBootApplication
public class TripInfoApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TripInfoApiApplication.class, args);
    }

    /**
     * Expose TripParser and TripAnalyzer as Spring beans so they can be
     * injected into DataLoader and AnalyticsController respectively.
     * Both are stateless, so a single shared instance is correct.
     */
    @Bean
    public TripParser tripParser() {
        return new TripParser();
    }

    @Bean
    public TripAnalyzer tripAnalyzer() {
        return new TripAnalyzer();
    }
}
