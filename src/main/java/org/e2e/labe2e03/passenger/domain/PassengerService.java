package org.e2e.labe2e03.passenger.domain;

import lombok.RequiredArgsConstructor;
import org.e2e.labe2e03.coordinate.domain.Coordinate;
import org.e2e.labe2e03.coordinate.exception.CoordinateNotFoundException;
import org.e2e.labe2e03.coordinate.infrastructure.CoordinateRepository;
import org.e2e.labe2e03.exception.ConflictException;
import org.e2e.labe2e03.passenger.dto.PassengerLocationDto;
import org.e2e.labe2e03.passenger.dto.PassengerRequestDto;
import org.e2e.labe2e03.passenger.exception.PassengerNotFoundException;
import org.e2e.labe2e03.passenger.infrastructure.PassengerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PassengerService {
    private final PassengerRepository passengerRepository;

    private final CoordinateRepository coordinateRepository;

    private final ModelMapper modelMapper;

    public Passenger getPassengerById(Long id) {
        return passengerRepository
                .findById(id)
                .orElseThrow(PassengerNotFoundException::new);
    }

    public void deletePassengerById(Long id) {
        if (!passengerRepository.existsById(id))
            throw new PassengerNotFoundException();
        passengerRepository.deleteById(id);
    }

    public Passenger addPassengerPlace(Long id, PassengerLocationDto passengerLocationDto) {
        Optional<Coordinate> coordinate =
                coordinateRepository
                        .findByLatitudeAndLongitude(passengerLocationDto.getCoordinate().getLatitude(),
                                passengerLocationDto.getCoordinate().getLongitude());

        Passenger passenger =
                passengerRepository
                        .findById(id)
                        .orElseThrow(PassengerNotFoundException::new);

        if (coordinate.isEmpty()) {
            Coordinate newCoordinate = coordinateRepository.save(modelMapper.map(passengerLocationDto.getCoordinate(),
                    Coordinate.class));
            passenger.addPlace(newCoordinate, passengerLocationDto.getDescription());
        } else {
            passenger.addPlace(coordinate.get(), passengerLocationDto.getDescription());
        }

        return passengerRepository.save(passenger);
    }

    public void deletePassengerPlace(Long id, Long coordinateId) {
        Passenger passenger = passengerRepository
                .findById(id)
                .orElseThrow(PassengerNotFoundException::new);
        Coordinate coordinate = coordinateRepository
                .findById(coordinateId)
                .orElseThrow(CoordinateNotFoundException::new);
        passenger.removePlace(coordinate);
        passengerRepository.save(passenger);
    }

    public List<PassengerLocationDto> getPassengerPlacesById(Long id) {
        Passenger passenger = passengerRepository
                .findById(id)
                .orElseThrow(PassengerNotFoundException::new);
        return passenger
                .getPlaces()
                .stream()
                .map(place -> modelMapper.map(place, PassengerLocationDto.class))
                .toList();
    }

    public Passenger createPassenger(PassengerRequestDto passengerRequestDto) {
        Passenger passenger = modelMapper.map(passengerRequestDto, Passenger.class);
        if (passengerRepository.existsByEmail(passenger.getEmail()))
            throw new ConflictException("Passenger with this email already exists");
        return passengerRepository.save(passenger);
    }
}