package org.e2e.labe2e03.ride.application;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.e2e.labe2e03.ride.domain.Ride;
import org.e2e.labe2e03.ride.domain.RideService;
import org.e2e.labe2e03.ride.dto.RideRequestDto;
import org.e2e.labe2e03.ride.dto.RideResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/ride")
@RequiredArgsConstructor
public class RideController {
    private final RideService rideService;

    @PostMapping
    public ResponseEntity<Ride> createRide(@Valid @RequestBody RideRequestDto rideRequestDto) {
        Ride createdRide = rideService.createRide(rideRequestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdRide.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdRide);
    }

    @PatchMapping("/{rideId}/assign/{driverId}")
    public ResponseEntity<Ride> assignDriverToRide(@PathVariable Long rideId, @PathVariable Long driverId) {
        Ride updatedRide = rideService.assignDriverToRide(rideId, driverId);
        return ResponseEntity.ok(updatedRide);
    }

    @GetMapping("/{passengerId}")
    public ResponseEntity<Page<RideResponseDto>> getRidesByPassengerId(@PathVariable Long passengerId,
                                                                       @RequestParam(defaultValue = "0") Integer page,
                                                                       @RequestParam(defaultValue = "10") Integer size) {
        Page<RideResponseDto> rides = rideService.getPassengerRides(passengerId, PageRequest.of(page, size));
        return ResponseEntity.ok(rides);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Ride> cancelRide(@PathVariable Long id) {
        Ride ride = rideService.cancelRide(id);
        return ResponseEntity.ok(ride);
    }
}