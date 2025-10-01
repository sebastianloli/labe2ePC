package org.e2e.labe2e03.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    private String id;
    private String bookingDate;
    private String flightId;
    private String flightNumber;
    private String customerId;
    private String customerFirstName;
    private String customerLastName;
}
