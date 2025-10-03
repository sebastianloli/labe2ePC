package org.e2e.labe2e03.controller;

import lombok.RequiredArgsConstructor;
import org.e2e.labe2e03.dto.request.FlightBookRequestDTO;
import org.e2e.labe2e03.dto.request.NewFlightManyRequestDTO;
import org.e2e.labe2e03.dto.request.NewFlightRequestDTO;
import org.e2e.labe2e03.dto.response.BookingResponseDTO;
import org.e2e.labe2e03.dto.response.FlightSearchResponseDTO;
import org.e2e.labe2e03.dto.response.NewFlightManyResponseDTO;
import org.e2e.labe2e03.dto.response.NewIdDTO;
import org.e2e.labe2e03.security.JwtUtil;
import org.e2e.labe2e03.service.BookingService;
import org.e2e.labe2e03.service.FlightService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(new NewIdDTO(String.valueOf(flightId)));
    }

    @PostMapping("/create-many")
    public ResponseEntity<NewFlightManyResponseDTO> createManyFlights(@RequestBody NewFlightManyRequestDTO dto) {
        // Operación asíncrona
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
        // Extraer userId del token JWT
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization token is required");
        }

        String token = authHeader.substring(7);
        Long userId = jwtUtil.extractUserId(token);

        Long bookingId = bookingService.bookFlight(dto, userId);
        return ResponseEntity.ok(new NewIdDTO(String.valueOf(bookingId)));
    }

    @GetMapping("/book/{id}")
    public ResponseEntity<BookingResponseDTO> getBooking(@PathVariable Long id) {
        return bookingService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
