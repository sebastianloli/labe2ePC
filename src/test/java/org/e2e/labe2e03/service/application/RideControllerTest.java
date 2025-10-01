package org.e2e.labe2e03.service.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.e2e.labe2e03.service.domain.Ride;
import org.e2e.labe2e03.service.domain.RideService;
import org.e2e.labe2e03.service.dto.RideRequestDto;
import org.e2e.labe2e03.service.dto.RideResponseDto;
import org.e2e.labe2e03.service.domain.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RideController.class)
class RideControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RideService rideService;

    @MockBean
    ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ObjectMapper objectMapper;

    private Ride testRide;
    private RideRequestDto testRideDto;

    @BeforeEach
    void setUp() {
        testRide = new Ride();
        testRide.setId(1L);
        testRide.setStatus(Status.REQUESTED);
        testRide.setPrice(25.0);

        testRideDto = new RideRequestDto();
        testRideDto.setPassengerId(1L);
        testRideDto.setDriverId(1L);
    }

    // Test 1:
    @Test
    void shouldReturnCreated_WhenPassengerBookRideWithValidData() throws Exception {
        when(rideService.createRide(any(RideRequestDto.class))).thenReturn(testRide);

        String requestBody = """
            {
                "originName": "Lima Centro",
                "destinationName": "Arequipa Plaza",
                "status": "REQUESTED",
                "price": 25.0,
                "originCoordinates": {
                    "latitude": -12.0, 
                    "longitude": -77.0
                },
                "destinationCoordinates": {
                    "latitude": -16.4, 
                    "longitude": -71.5
                },
                "passengerId": 1,
                "driverId": 1
            }
            """;

        mockMvc.perform(post("/ride")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/ride/1")));

        verify(rideService, times(1)).createRide(any(RideRequestDto.class));
    }

    // Test 2:
    @Test
    void shouldReturnOk_WhenCancelExistingRide() throws Exception {
        testRide.setStatus(Status.CANCELLED);
        when(rideService.cancelRide(1L)).thenReturn(testRide);

        mockMvc.perform(patch("/ride/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(rideService, times(1)).cancelRide(1L);
    }

    // Test 3:
    @Test
    void shouldReturnRides_WhenGetRideByUser() throws Exception {
        RideResponseDto responseDto = new RideResponseDto();
        responseDto.setId(1L);
        responseDto.setPrice(25.0);

        List<RideResponseDto> rides = Arrays.asList(responseDto);
        Page<RideResponseDto> ridePage = new PageImpl<>(rides, PageRequest.of(0, 10), 1);

        when(rideService.getPassengerRides(eq(1L), any()))
                .thenReturn(ridePage);

        mockMvc.perform(get("/ride/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(rideService, times(1)).getPassengerRides(eq(1L), any());
    }

    // Test 4:
    @Test
    void shouldReturnOk_WhenDriverAssignToAvailableRide() throws Exception {
        testRide.setStatus(Status.ACCEPTED);
        when(rideService.assignDriverToRide(1L, 2L)).thenReturn(testRide);

        mockMvc.perform(patch("/ride/1/assign/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        verify(rideService, times(1)).assignDriverToRide(1L, 2L);
    }
}