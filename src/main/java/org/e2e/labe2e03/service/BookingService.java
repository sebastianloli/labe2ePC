package org.e2e.labe2e03.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.e2e.labe2e03.dto.request.FlightBookRequestDTO;
import org.e2e.labe2e03.dto.response.BookingResponseDTO;
import org.e2e.labe2e03.entity.Booking;
import org.e2e.labe2e03.entity.Flight;
import org.e2e.labe2e03.entity.User;
import org.e2e.labe2e03.repository.BookingRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightService flightService;
    private final UserService userService;
    private final EmailService emailService;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Transactional
    public Long bookFlight(FlightBookRequestDTO dto, Long userId) {
        if (dto.getFlightId() == null) {
            throw new IllegalArgumentException("Flight ID is mandatory");
        }

        Flight flight = flightService.findById(dto.getFlightId())
                .orElseThrow(() -> new IllegalArgumentException("Flight not found"));

        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Must-Have: Validar que no esté oversold
        long currentBookings = bookingRepository.findByFlightId(flight.getId()).size();
        if (currentBookings >= flight.getAvailableSeats()) {
            throw new IllegalArgumentException("Flight cannot be oversold");
        }

        // Nice-to-Have: Validar que el vuelo no esté en el pasado o en tránsito
        LocalDateTime now = LocalDateTime.now();
        if (flight.getEstDepartureTime().isBefore(now)) {
            throw new IllegalArgumentException("Flight cannot be in the past");
        }
        if (flight.getEstArrivalTime().isBefore(now) && flight.getEstDepartureTime().isAfter(now)) {
            throw new IllegalArgumentException("Flight cannot be in transit");
        }

        // Nice-to-Have: Validar que no haya overlap
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                userId,
                flight.getEstDepartureTime(),
                flight.getEstArrivalTime()
        );

        if (!overlappingBookings.isEmpty()) {
            throw new IllegalArgumentException("Customer cannot book a flight that overlaps with another");
        }

        Booking booking = new Booking();
        booking.setBookingDate(LocalDateTime.now());
        booking.setFlight(flight);
        booking.setCustomer(user);
        booking.setCustomerFirstName(user.getFirstName());
        booking.setCustomerLastName(user.getLastName());

        Booking savedBooking = bookingRepository.save(booking);

        // Nice-to-Have: Enviar email de confirmación
        try {
            emailService.sendBookingConfirmation(savedBooking);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }

        return savedBooking.getId();
    }

    public Optional<BookingResponseDTO> findById(Long id) {
        return bookingRepository.findById(id).map(this::convertToDTO);
    }

    private BookingResponseDTO convertToDTO(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(String.valueOf(booking.getId()));

        // Truncar a segundos para evitar nanosegundos en el formato
        LocalDateTime truncated = booking.getBookingDate().withNano(0);
        dto.setBookingDate(truncated.format(ISO_FORMATTER));

        dto.setFlightId(String.valueOf(booking.getFlight().getId()));
        dto.setFlightNumber(booking.getFlight().getFlightNumber());
        dto.setCustomerId(String.valueOf(booking.getCustomer().getId()));
        dto.setCustomerFirstName(booking.getCustomerFirstName());
        dto.setCustomerLastName(booking.getCustomerLastName());
        return dto;
    }

    @Transactional
    public void deleteAll() {
        bookingRepository.deleteAll();
    }
}
