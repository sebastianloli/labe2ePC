package org.e2e.labe2e03.ride.exception;

import org.e2e.labe2e03.exception.ResourceNotFoundException;

public class RideNotFoundException extends ResourceNotFoundException {
    public RideNotFoundException() {
        super("Ride not found");
    }
}
