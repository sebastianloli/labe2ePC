package org.e2e.labe2e03.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchResponseDTO {
    @JsonProperty("items")
    private List<FlightDTO> items;
}
