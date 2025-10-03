package org.e2e.labe2e03.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.e2e.labe2e03.dto.request.FlightBookRequestDTO;
import org.e2e.labe2e03.dto.request.NewFlightManyRequestDTO;
import org.e2e.labe2e03.dto.request.NewFlightRequestDTO;
import org.e2e.labe2e03.dto.response.*;
import org.e2e.labe2e03.security.JwtUtil;
import org.e2e.labe2e03.service.BookingService;
import org.e2e.labe2e03.service.FlightService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;
    private final BookingService bookingService;
    private final JwtUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<NewIdDTO> createFlight(@RequestBody NewFlightRequestDTO dto) {
        Long flightId = flightService.createFlight(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new NewIdDTO(String.valueOf(flightId)));
    }

    @PostMapping("/create-many")
    public ResponseEntity<NewFlightManyResponseDTO> createManyFlights(@RequestBody NewFlightManyRequestDTO dto) {
        flightService.createFlightsAsync(dto.getFlights());
        return ResponseEntity.ok(new NewFlightManyResponseDTO("Flights creation started"));
    }

    @GetMapping("/search")
    public ResponseEntity<FlightSearchResponseDTO> searchFlights(
            @RequestParam(required = false) String flightNumber,
            @RequestParam(required = false) String airlineName,
            @RequestParam(required = false) String estDepartureTimeFrom,
            @RequestParam(required = false) String estDepartureTimeTo) {

        var flights = flightService.searchFlights(flightNumber, airlineName,
                estDepartureTimeFrom, estDepartureTimeTo);
        return ResponseEntity.ok(new FlightSearchResponseDTO(flights));
    }

    @PostMapping("/book")
    public ResponseEntity<NewIdDTO> bookFlight(@RequestBody FlightBookRequestDTO dto,
                                               HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization token is required");
        }

        String token = authHeader.substring(7);
        Long userId = jwtUtil.extractUserId(token);

        Long bookingId = bookingService.bookFlight(dto, userId);
        return ResponseEntity.ok(new NewIdDTO(String.valueOf(bookingId))); // Cambiado a .ok()
    }

    @GetMapping("/book/{id}")
    public ResponseEntity<BookingResponseDTO> getBooking(@PathVariable Long id) {
        return bookingService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
