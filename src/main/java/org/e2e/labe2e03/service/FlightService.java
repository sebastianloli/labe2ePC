package org.e2e.labe2e03.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.e2e.labe2e03.dto.request.NewFlightRequestDTO;
import org.e2e.labe2e03.dto.response.FlightDTO;
import org.e2e.labe2e03.entity.Flight;
import org.e2e.labe2e03.repository.FlightRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlightService {

    private final FlightRepository flightRepository;

    private static final Pattern FLIGHT_NUMBER_PATTERN = Pattern.compile("^[A-Z0-9]{1,6}$");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Transactional
    public Long createFlight(NewFlightRequestDTO dto) {
        validateFlight(dto);

        Flight flight = new Flight();
        flight.setAirlineName(dto.getAirlineName());
        flight.setFlightNumber(dto.getFlightNumber());
        flight.setEstDepartureTime(LocalDateTime.parse(dto.getEstDepartureTime(), ISO_FORMATTER));
        flight.setEstArrivalTime(LocalDateTime.parse(dto.getEstArrivalTime(), ISO_FORMATTER));
        flight.setAvailableSeats(dto.getAvailableSeats());

        Flight savedFlight = flightRepository.save(flight);
        return savedFlight.getId();
    }

    @Async
    @Transactional
    public void createFlightsAsync(List<NewFlightRequestDTO> flights) {
        log.info("Starting async creation of {} flights", flights.size());

        for (NewFlightRequestDTO dto : flights) {
            try {
                validateFlight(dto);

                Flight flight = new Flight();
                flight.setAirlineName(dto.getAirlineName());
                flight.setFlightNumber(dto.getFlightNumber());
                flight.setEstDepartureTime(LocalDateTime.parse(dto.getEstDepartureTime(), ISO_FORMATTER));
                flight.setEstArrivalTime(LocalDateTime.parse(dto.getEstArrivalTime(), ISO_FORMATTER));
                flight.setAvailableSeats(dto.getAvailableSeats());

                flightRepository.save(flight);
                log.info("Created flight: {}", flight.getFlightNumber());
            } catch (Exception e) {
                log.error("Error creating flight {}: {}", dto.getFlightNumber(), e.getMessage());
            }
        }

        log.info("Finished async creation of flights");
    }

    private void validateFlight(NewFlightRequestDTO dto) {
        // Campos mandatorios
        if (dto.getAirlineName() == null || dto.getAirlineName().trim().isEmpty()) {
            throw new IllegalArgumentException("Airline name is mandatory");
        }
        if (dto.getFlightNumber() == null || dto.getFlightNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Flight number is mandatory");
        }
        if (dto.getEstDepartureTime() == null || dto.getEstDepartureTime().trim().isEmpty()) {
            throw new IllegalArgumentException("Estimated departure time is mandatory");
        }
        if (dto.getEstArrivalTime() == null || dto.getEstArrivalTime().trim().isEmpty()) {
            throw new IllegalArgumentException("Estimated arrival time is mandatory");
        }
        if (dto.getAvailableSeats() == null) {
            throw new IllegalArgumentException("Available seats is mandatory");
        }

        // Validar formato de flight number (A-Z 0-9, max 6 caracteres)
        if (!FLIGHT_NUMBER_PATTERN.matcher(dto.getFlightNumber()).matches()) {
            throw new IllegalArgumentException("Flight number must be A-Z 0-9, up to 6 characters");
        }

        // Validar que departure < arrival
        LocalDateTime departure = LocalDateTime.parse(dto.getEstDepartureTime(), ISO_FORMATTER);
        LocalDateTime arrival = LocalDateTime.parse(dto.getEstArrivalTime(), ISO_FORMATTER);

        if (!departure.isBefore(arrival)) {
            throw new IllegalArgumentException("Estimated departure time must be before estimated arrival time");
        }

        // Validar available seats > 0
        if (dto.getAvailableSeats() <= 0) {
            throw new IllegalArgumentException("Available seats must be greater than 0");
        }

        // Validar que el flight number no se repita
        if (flightRepository.existsByFlightNumber(dto.getFlightNumber())) {
            throw new IllegalArgumentException("Flight number cannot be repeated");
        }
    }

    public List<FlightDTO> searchFlights(String flightNumber, String airlineName,
                                         String estDepartureTimeFrom, String estDepartureTimeTo) {
        LocalDateTime fromDate = null;
        LocalDateTime toDate = null;

        if (estDepartureTimeFrom != null && !estDepartureTimeFrom.trim().isEmpty()) {
            fromDate = LocalDateTime.parse(estDepartureTimeFrom, ISO_FORMATTER);
        }

        if (estDepartureTimeTo != null && !estDepartureTimeTo.trim().isEmpty()) {
            toDate = LocalDateTime.parse(estDepartureTimeTo, ISO_FORMATTER);
        }

        List<Flight> flights = flightRepository.searchFlights(flightNumber, airlineName, fromDate, toDate);

        return flights.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private FlightDTO convertToDTO(Flight flight) {
        FlightDTO dto = new FlightDTO();
        dto.setId(String.valueOf(flight.getId()));
        dto.setAirlineName(flight.getAirlineName());
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setEstDepartureTime(flight.getEstDepartureTime().format(ISO_FORMATTER));
        dto.setEstArrivalTime(flight.getEstArrivalTime().format(ISO_FORMATTER));
        dto.setAvailableSeats(flight.getAvailableSeats());
        return dto;
    }

    public Optional<Flight> findById(Long id) {
        return flightRepository.findById(id);
    }

    @Transactional
    public void deleteAll() {
        flightRepository.deleteAll();
    }
}
