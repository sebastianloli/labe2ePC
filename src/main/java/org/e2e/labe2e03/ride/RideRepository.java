package org.e2e.labe2e03.ride;

import org.e2e.labe2e03.coordinate.domain.Coordinate;
import org.e2e.labe2e03.ride.domain.Ride;
import org.e2e.labe2e03.ride.domain.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;

public interface RideRepository extends JpaRepository<Ride, Long> {
    Page<Ride> findAllByPassengerIdAndStatus(Long passengerId,
                                             Status status,
                                             Pageable pageable);
    Page<Ride> findAllByArrivalDateAndDestinationCoordinates(
            ZonedDateTime arrivalDate, Coordinate destinationCoordinates, Pageable pageable);
}
