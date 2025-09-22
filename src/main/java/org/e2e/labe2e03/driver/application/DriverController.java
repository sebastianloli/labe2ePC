package org.e2e.labe2e03.driver.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.e2e.labe2e03.driver.domain.Driver;
import org.e2e.labe2e03.driver.domain.DriverService;
import org.e2e.labe2e03.driver.dto.DriverDto;
import org.e2e.labe2e03.driver.dto.DriverRequestDto;
import org.e2e.labe2e03.vehicle.dto.VehicleBasicDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/driver")
@RequiredArgsConstructor
public class DriverController {
    private final DriverService driverService;

    @GetMapping("/{id}")
    public ResponseEntity<DriverDto> getDriverById(@PathVariable Long id) {
        DriverDto driverDto = driverService.getDriverById(id);
        return ResponseEntity.ok(driverDto);
    }

    @PostMapping
    public ResponseEntity<Driver> createDriver(@Valid @RequestBody DriverRequestDto driverRequestDto) {
        Driver createdDriver = driverService.createDriver(driverRequestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdDriver.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdDriver);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Driver> deleteDriverById(@PathVariable Long id) {
        driverService.deleteDriverById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Driver> updateDriver(@PathVariable Long id, @Valid @RequestBody DriverDto driverDto) {
        Driver updatedDriver = driverService.updateDriver(id, driverDto);
        return ResponseEntity.ok(updatedDriver);
    }

    @PatchMapping("/{id}/location")
    public ResponseEntity<Driver> updateDriverLocation(@PathVariable Long id,
                                                       @RequestParam Double latitude,
                                                       @RequestParam Double longitude) {
        Driver updatedDriver = driverService.updateDriverLocation(id, latitude, longitude);
        return ResponseEntity.ok(updatedDriver);
    }

    @PatchMapping("/{id}/car")
    public ResponseEntity<DriverDto> updateDriverCar(@PathVariable Long id,
                                                     @Valid @RequestBody VehicleBasicDto vehicleBasicDto) {
        DriverDto driverDto = driverService.updateDriverCar(id, vehicleBasicDto);
        return ResponseEntity.ok(driverDto);
    }
}
