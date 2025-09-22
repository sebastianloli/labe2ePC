package org.e2e.labe2e03.user.infrastructure;

import org.e2e.labe2e03.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseUserRepository<T extends User> extends JpaRepository<T, Long> {
    boolean existsByEmail(String email);
}