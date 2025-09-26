package org.e2e.labe2e03.ride.event;

import java.time.ZonedDateTime;

public record RideCreatedEvent(
        Long rideId,
        String passengerEmail,
        String destinationName,
        ZonedDateTime departureDate,
        Double price
) {}