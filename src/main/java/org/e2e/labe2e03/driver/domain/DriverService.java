package org.e2e.labe2e03.driver.domain;

import lombok.RequiredArgsConstructor;
import org.e2e.labe2e03.coordinate.domain.Coordinate;
import org.e2e.labe2e03.coordinate.infrastructure.CoordinateRepository;
import org.e2e.labe2e03.driver.dto.DriverDto;
import org.e2e.labe2e03.driver.dto.DriverRequestDto;
import org.e2e.labe2e03.driver.exception.DriverNotFoundException;
import org.e2e.labe2e03.driver.infrastructure.DriverRepository;
import org.e2e.labe2e03.exception.ConflictException;
import org.e2e.labe2e03.vehicle.dto.VehicleBasicDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;

    private final CoordinateRepository coordinateRepository;

    private final ModelMapper modelMapper;

    public DriverDto getDriverById(Long id) {
        Driver driver = driverRepository
                .findById(id)
                .orElseThrow(DriverNotFoundException::new);
        return modelMapper.map(driver, DriverDto.class);
    }

    public Driver createDriver(DriverRequestDto driverRequestDto) {
        if (driverRepository.existsByEmail(driverRequestDto.getEmail()))
            throw new ConflictException("Driver with this email already exists");
        return driverRepository.save(modelMapper.map(driverRequestDto, Driver.class));
    }

    public void deleteDriverById(Long id) {
        driverRepository.deleteById(id);
    }

    public Driver updateDriver(Long id, DriverDto driverDto) {
        Driver existingDriver = driverRepository
                .findById(id)
                .orElseThrow(DriverNotFoundException::new);
        modelMapper.map(driverDto, existingDriver);
        return driverRepository.save(existingDriver);
    }

    public Driver updateDriverLocation(Long id, Double latitude, Double longitude) {
        Driver existingDriver = driverRepository
                .findById(id)
                .orElseThrow(DriverNotFoundException::new);
        Coordinate coordinate = new Coordinate();
        coordinate.setLatitude(latitude);
        coordinate.setLongitude(longitude);
        coordinateRepository.save(coordinate);
        existingDriver.setCoordinate(coordinate);
        return driverRepository.save(existingDriver);
    }

    public DriverDto updateDriverCar(Long id, VehicleBasicDto vehicleBasicDto) {
        Driver existingDriver = driverRepository
                .findById(id)
                .orElseThrow(DriverNotFoundException::new);
        modelMapper.map(vehicleBasicDto, existingDriver.getVehicle());
        return modelMapper.map(driverRepository.save(existingDriver), DriverDto.class);
    }
}