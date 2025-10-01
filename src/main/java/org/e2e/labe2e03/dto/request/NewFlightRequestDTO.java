package org.e2e.labe2e03.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewFlightRequestDTO {
    private String airlineName;
    private String flightNumber;
    private String estDepartureTime;
    private String estArrivalTime;
    private Integer availableSeats;
}
