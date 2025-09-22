package org.e2e.labe2e03.passenger.infrastructure;

import jakarta.transaction.Transactional;
import org.e2e.labe2e03.passenger.domain.Passenger;
import org.e2e.labe2e03.user.infrastructure.BaseUserRepository;

@Transactional
public interface PassengerRepository extends BaseUserRepository<Passenger> {
}