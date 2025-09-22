package org.e2e.labe2e03.passenger.exception;

import org.e2e.labe2e03.exception.ResourceNotFoundException;

public class PassengerNotFoundException extends ResourceNotFoundException {
    public PassengerNotFoundException() {
        super("Passenger not found");
    }
}
