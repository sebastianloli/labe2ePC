package org.e2e.labe2e03.driver.exception;

import org.e2e.labe2e03.exception.ResourceNotFoundException;

public class DriverNotFoundException extends ResourceNotFoundException {
    public DriverNotFoundException() {
        super("Driver not found");
    }
}
