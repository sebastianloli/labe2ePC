package org.e2e.labe2e03.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchResponseDTO {
    private List<FlightDTO> flights;
}
