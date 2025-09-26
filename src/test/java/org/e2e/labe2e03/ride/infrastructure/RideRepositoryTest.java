package org.e2e.labe2e03.ride.infrastructure;

import org.e2e.labe2e03.config.PostgresTestContainerConfig;
import org.e2e.labe2e03.coordinate.domain.Coordinate;
import org.e2e.labe2e03.coordinate.infrastructure.CoordinateRepository;
import org.e2e.labe2e03.driver.domain.Category;
import org.e2e.labe2e03.driver.domain.Driver;
import org.e2e.labe2e03.driver.infrastructure.DriverRepository;
import org.e2e.labe2e03.passenger.domain.Passenger;
import org.e2e.labe2e03.passenger.infrastructure.PassengerRepository;
import org.e2e.labe2e03.ride.RideRepository;
import org.e2e.labe2e03.ride.domain.Ride;
import org.e2e.labe2e03.ride.domain.Status;
import org.e2e.labe2e03.user.domain.Role;
import org.e2e.labe2e03.vehicle.domain.Vehicle;
import org.e2e.labe2e03.vehicle.infrastructure.VehicleRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@Import(PostgresTestContainerConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RideRepositoryTest {

    @Autowired
    RideRepository rideRepository;
    @Autowired
    CoordinateRepository coordinateRepository;
    @Autowired
    PassengerRepository passengerRepository;
    @Autowired
    DriverRepository driverRepository;
    @Autowired
    VehicleRepository vehicleRepository;

    Coordinate lima, arequipa;
    Passenger passenger;
    Driver driver;
    Vehicle vehicle;
    Random random = new Random();

    @BeforeEach
    void setUp() {
        rideRepository.deleteAll();
        driverRepository.deleteAll();
        passengerRepository.deleteAll();
        vehicleRepository.deleteAll();
        coordinateRepository.deleteAll();

        lima = new Coordinate(-12.0464, -77.0428);
        lima = coordinateRepository.save(lima);

        arequipa = new Coordinate(-16.4090, -71.5375);
        arequipa = coordinateRepository.save(arequipa);

        passenger = new Passenger();
        passenger.setPassword("password123");
        passenger.setFirstName("Juan");
        passenger.setLastName("Pérez");
        passenger.setPhoneNumber("999999999");
        passenger.setCoordinate(lima);
        passenger.setEmail("juan@test.com");
        passenger.setRole(Role.PASSENGER);
        passenger = passengerRepository.save(passenger);

        vehicle = new Vehicle();
        vehicle.setBrand("Toyota");
        vehicle.setModel("Corolla");
        vehicle.setLicensePlate("ABC-123");
        vehicle.setColor("Blanco");
        vehicle.setCapacity(4);
        vehicle.setFabricationYear(2020);
        vehicle = vehicleRepository.save(vehicle);

        driver = new Driver();
        driver.setPassword("password456");
        driver.setFirstName("María");
        driver.setLastName("García");
        driver.setPhoneNumber("987654321");
        driver.setCoordinate(lima);
        driver.setEmail("maria@test.com");
        driver.setRole(Role.DRIVER);
        driver.setVehicle(vehicle);
        driver.setCategory(Category.X);
        driver = driverRepository.save(driver);
    }

    // Test 1:
    @Test
    void shouldCreateRide_WhenValidRideData() {
        Ride ride = createValidRide();

        Ride savedRide = rideRepository.save(ride);

        assertThat(savedRide).isNotNull();
        assertThat(savedRide.getId()).isNotNull();
        assertThat(savedRide.getPassenger()).isEqualTo(passenger);
        assertThat(savedRide.getDriver()).isEqualTo(driver);
        assertThat(savedRide.getStatus()).isEqualTo(Status.REQUESTED);
        assertThat(savedRide.getPrice()).isEqualTo(25.0);
    }

    // Test 2:
    @Test
    void shouldReturnRide_WhenSearchingByExistingId() {
        Ride savedRide = rideRepository.save(createValidRide());

        Optional<Ride> foundRide = rideRepository.findById(savedRide.getId());

        assertThat(foundRide).isPresent();
        assertThat(foundRide.get().getId()).isEqualTo(savedRide.getId());
        assertThat(foundRide.get().getPassenger().getEmail()).isEqualTo("juan@test.com");
    }

    // Test 3:
    @Test
    void shouldDeleteRide_WhenValidId() {
        Ride savedRide = rideRepository.save(createValidRide());
        Long rideId = savedRide.getId();

        rideRepository.deleteById(rideId);

        Optional<Ride> deletedRide = rideRepository.findById(rideId);
        assertThat(deletedRide).isEmpty();
    }

    // Test 4:
    @Test
    void shouldReturnRides_WhenSearchingByArrivalDateAndDestinationCoordinates() {
        ZonedDateTime specificDate = ZonedDateTime.now().plusHours(5);

        Coordinate testDestination = new Coordinate(-16.4090, -71.5375);
        testDestination = coordinateRepository.save(testDestination);

        Ride ride = createValidRide();
        ride.setArrivalDate(specificDate);
        ride.setDestinationCoordinates(testDestination);
        rideRepository.save(ride);

        Coordinate differentDestination = new Coordinate(-10.0, -75.0);
        differentDestination = coordinateRepository.save(differentDestination);

        Ride anotherRide = createValidRide();
        anotherRide.setArrivalDate(specificDate);
        anotherRide.setDestinationCoordinates(differentDestination);
        rideRepository.save(anotherRide);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Ride> ridesFound = rideRepository.findAllByArrivalDateAndDestinationCoordinates(
                specificDate, testDestination, pageable);

        assertThat(ridesFound.getContent()).hasSize(1);
        assertThat(ridesFound.getContent().get(0).getDestinationCoordinates()).isEqualTo(testDestination);
        assertThat(ridesFound.getContent().get(0).getArrivalDate()).isEqualTo(specificDate);
    }

    // Test 5:
    @Test
    void shouldReturnRides_WhenSearchingByPassengerIdAndStatus() {
        Ride acceptedRide = createValidRide();
        acceptedRide.setStatus(Status.ACCEPTED);
        rideRepository.save(acceptedRide);

        Ride requestedRide = createValidRide();
        requestedRide.setStatus(Status.REQUESTED);
        rideRepository.save(requestedRide);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Ride> acceptedRides = rideRepository.findAllByPassengerIdAndStatus(
                passenger.getId(), Status.ACCEPTED, pageable);

        assertThat(acceptedRides.getContent()).hasSize(1);
        assertThat(acceptedRides.getContent().get(0).getStatus()).isEqualTo(Status.ACCEPTED);
        assertThat(acceptedRides.getContent().get(0).getPassenger().getId()).isEqualTo(passenger.getId());
    }

    private Ride createValidRide() {
        Coordinate originCoord = new Coordinate(
                -12.0464 + (random.nextDouble() * 0.01),
                -77.0428 + (random.nextDouble() * 0.01)
        );
        originCoord = coordinateRepository.save(originCoord);

        Coordinate destinationCoord = new Coordinate(
                -16.4090 + (random.nextDouble() * 0.01),
                -71.5375 + (random.nextDouble() * 0.01)
        );
        destinationCoord = coordinateRepository.save(destinationCoord);

        Ride ride = new Ride();
        ride.setPassenger(passenger);
        ride.setDriver(driver);
        ride.setOriginCoordinates(originCoord);
        ride.setDestinationCoordinates(destinationCoord);
        ride.setOriginName("Lima");
        ride.setDestinationName("Arequipa");
        ride.setDepartureDate(ZonedDateTime.now().plusHours(2));
        ride.setArrivalDate(ZonedDateTime.now().plusHours(4));
        ride.setStatus(Status.REQUESTED);
        ride.setPrice(25.0);
        ride.setCreatedAt(ZonedDateTime.now());
        return ride;
    }
}