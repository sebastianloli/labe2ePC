package org.e2e.labe2e03.controller;

import lombok.RequiredArgsConstructor;
import org.e2e.labe2e03.service.BookingService;
import org.e2e.labe2e03.service.FlightService;
import org.e2e.labe2e03.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cleanup")
@RequiredArgsConstructor
public class CleanupController {

    private final UserService userService;
    private final FlightService flightService;
    private final BookingService bookingService;

    @DeleteMapping
    public ResponseEntity<Void> cleanup() {
        // Orden importante: primero bookings, luego flights y users
        bookingService.deleteAll();
        flightService.deleteAll();
        userService.deleteAll();

        return ResponseEntity.ok().build();
    }
}
