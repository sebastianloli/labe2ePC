package org.e2e.labe2e03.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "bookingDate", "flightId", "flightNumber", "customerId", "customerFirstName", "customerLastName"})
public class BookingResponseDTO {
    private String id;
    private String bookingDate;
    private String flightId;
    private String flightNumber;
    private String customerId;
    private String customerFirstName;
    private String customerLastName;
    private String estArrivalTime;
    private String estDepartureTime;
}
