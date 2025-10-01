package org.e2e.labe2e03.repository;

import org.e2e.labe2e03.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByFlightId(Long flightId);

    List<Booking> findByCustomerId(Long customerId);

    @Query("SELECT b FROM Booking b WHERE b.customer.id = :customerId AND " +
            "((b.flight.estDepartureTime <= :arrivalTime AND b.flight.estArrivalTime >= :departureTime))")
    List<Booking> findOverlappingBookings(@Param("customerId") Long customerId,
                                          @Param("departureTime") java.time.LocalDateTime departureTime,
                                          @Param("arrivalTime") java.time.LocalDateTime arrivalTime);
}
