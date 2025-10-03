package org.e2e.labe2e03.service;

import lombok.extern.slf4j.Slf4j;
import org.e2e.labe2e03.entity.Booking;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class EmailService {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public void sendBookingConfirmation(Booking booking) {
        String filename = "flight_booking_email_" + booking.getId() + ".txt";

        StringBuilder emailContent = new StringBuilder();
        emailContent.append("Hello ").append(booking.getCustomerFirstName())
                .append(" ").append(booking.getCustomerLastName()).append(",\n\n");
        emailContent.append("Your booking was successful!\n\n");
        emailContent.append("The booking is for flight ").append(booking.getFlight().getFlightNumber())
                .append(" with departure date of ")
                .append(booking.getFlight().getEstDepartureTime().format(ISO_FORMATTER))
                .append(" and arrival date ")
                .append(booking.getFlight().getEstArrivalTime().format(ISO_FORMATTER))
                .append("\n\n");
        emailContent.append("The booking was registered at ")
                .append(booking.getBookingDate().format(ISO_FORMATTER))
                .append(".\n\n");
        emailContent.append("Bon Voyage!\n");
        emailContent.append("Fly Away Travel");

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(emailContent.toString());
            log.info("Email confirmation saved to: {}", filename);
        } catch (IOException e) {
            log.error("Error writing email file: {}", e.getMessage());
            throw new RuntimeException("Failed to send email confirmation", e);
        }
    }
}