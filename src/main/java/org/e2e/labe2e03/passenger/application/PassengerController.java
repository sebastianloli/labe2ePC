package org.e2e.labe2e03.passenger.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.e2e.labe2e03.passenger.domain.Passenger;
import org.e2e.labe2e03.passenger.domain.PassengerService;
import org.e2e.labe2e03.passenger.dto.PassengerLocationDto;
import org.e2e.labe2e03.passenger.dto.PassengerRequestDto;
import org.e2e.labe2e03.passenger.dto.PassengerResponseDto;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/passenger")
@RequiredArgsConstructor
public class PassengerController {
    private final PassengerService passengerService;

    private final ModelMapper modelMapper;

    @GetMapping("/{id}")
    ResponseEntity<PassengerResponseDto> getPassengerById(@PathVariable Long id) {
        Passenger passenger = passengerService.getPassengerById(id);
        PassengerResponseDto passengerResponseDto = modelMapper.map(passenger, PassengerResponseDto.class);
        return ResponseEntity.ok(passengerResponseDto);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deletePassengerById(@PathVariable Long id) {
        passengerService.deletePassengerById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Passenger> addPassengerPlace(@PathVariable Long id,
                                                       @Valid @RequestBody PassengerLocationDto passengerLocationDto) {
        Passenger updatedPassenger = passengerService.addPassengerPlace(id, passengerLocationDto);
        return ResponseEntity.ok(updatedPassenger);
    }

    @GetMapping("/{id}/places")
    public ResponseEntity<List<PassengerLocationDto>> getPassengerPlacesById(@PathVariable Long id) {
        List<PassengerLocationDto> places = passengerService.getPassengerPlacesById(id);
        return ResponseEntity.ok(places);
    }

    @DeleteMapping("/{id}/places/{coordinateId}")
    public ResponseEntity<Void> deletePassengerPlace(@PathVariable Long id, @PathVariable Long coordinateId) {
        passengerService.deletePassengerPlace(id, coordinateId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Passenger> createPassenger(@Valid @RequestBody PassengerRequestDto passengerRequestDto) {
        Passenger passenger = passengerService.createPassenger(passengerRequestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(passenger.getId())
                .toUri();
        return ResponseEntity.created(location).body(passenger);
    }
}