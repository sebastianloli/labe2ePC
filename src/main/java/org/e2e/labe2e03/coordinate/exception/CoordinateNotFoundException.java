package org.e2e.labe2e03.coordinate.exception;

import org.e2e.labe2e03.exception.ResourceNotFoundException;

public class CoordinateNotFoundException extends ResourceNotFoundException {
    public CoordinateNotFoundException() {
        super("Coordinate not found");
    }
}
