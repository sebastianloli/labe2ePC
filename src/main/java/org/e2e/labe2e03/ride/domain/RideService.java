package org.e2e.labe2e03.ride.domain;

import lombok.RequiredArgsConstructor;
import org.e2e.labe2e03.driver.domain.Driver;
import org.e2e.labe2e03.driver.exception.DriverNotFoundException;
import org.e2e.labe2e03.driver.infrastructure.DriverRepository;
import org.e2e.labe2e03.exception.BadRequestException;
import org.e2e.labe2e03.passenger.domain.Passenger;
import org.e2e.labe2e03.passenger.exception.PassengerNotFoundException;
import org.e2e.labe2e03.passenger.infrastructure.PassengerRepository;
import org.e2e.labe2e03.ride.dto.RideRequestDto;
import org.e2e.labe2e03.ride.dto.RideResponseDto;
import org.e2e.labe2e03.ride.exception.RideNotFoundException;
import org.e2e.labe2e03.ride.RideRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.e2e.labe2e03.ride.event.RideCreatedEvent;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RideService {
    private final PassengerRepository passengerRepository;

    private final RideRepository rideRepository;

    private final DriverRepository driverRepository;

    private final ModelMapper modelMapper;

    private final ApplicationEventPublisher events;

    public Ride createRide(RideRequestDto rideRequestDto) {
        Passenger passenger = passengerRepository
                .findById(rideRequestDto.getPassengerId())
                .orElseThrow(PassengerNotFoundException::new);
        Driver driver = driverRepository
                .findById(rideRequestDto.getDriverId())
                .orElseThrow(DriverNotFoundException::new);

        if (Objects.equals(rideRequestDto.getDestinationCoordinates().getLatitude(),
                rideRequestDto.getOriginCoordinates().getLatitude()) &&
                Objects.equals(rideRequestDto.getDestinationCoordinates().getLongitude(),
                        rideRequestDto.getOriginCoordinates().getLongitude())) {
            throw new BadRequestException("Origin and destination coordinates cannot be the same");
        }

        Ride ride = modelMapper.map(rideRequestDto, Ride.class);
        ride.setPassenger(passenger);
        ride.setDriver(driver);

        Ride saved = rideRepository.save(ride);
        String email = tryGetPassengerEmail(passenger);

        events.publishEvent(new RideCreatedEvent(
                saved.getId(),
                email,
                saved.getDestinationName(),
                saved.getDepartureDate(),
                saved.getPrice()
        ));

        return saved;
    }

    public Ride assignDriverToRide(Long rideId, Long driverId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(RideNotFoundException::new);
        Driver driver = driverRepository.findById(driverId).orElseThrow(DriverNotFoundException::new);
        ride.setDriver(driver);
        ride.setStatus(Status.ACCEPTED);
        return rideRepository.save(ride);
    }

    public Page<RideResponseDto> getPassengerRides(Long passengerId, Pageable pageable) {
        Passenger passenger =
                passengerRepository
                        .findById(passengerId)
                        .orElseThrow(PassengerNotFoundException::new);
        Page<Ride> rides = rideRepository.findAllByPassengerIdAndStatus(passenger.getId(), Status.COMPLETED, pageable);
        return rides.map(ride -> modelMapper.map(ride, RideResponseDto.class));
    }

    public Ride cancelRide(Long id) {
        Ride ride = rideRepository.findById(id).orElseThrow(RideNotFoundException::new);
        ride.setStatus(Status.CANCELLED);
        return rideRepository.save(ride);
    }

    private String tryGetPassengerEmail(Passenger p) {
        try {
            var m = p.getClass().getMethod("getEmail");
            Object v = m.invoke(p);
            if (v != null) return v.toString();
        } catch (Exception ignore) { /* pasa a opción B */ }
        try {
            var user = p.getClass().getMethod("getUser").invoke(p);
            if (user != null) {
                var email = user.getClass().getMethod("getEmail").invoke(user);
                if (email != null) return email.toString();
            }
        } catch (Exception ignore) {}
        return "no-reply@example.com";
    }
}