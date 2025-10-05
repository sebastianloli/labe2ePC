package org.e2e.labe2e03.service;

import lombok.RequiredArgsConstructor;
import org.e2e.labe2e03.dto.request.RegisterUserDTO;
import org.e2e.labe2e03.entity.User;
import org.e2e.labe2e03.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    @Transactional
    public Long registerUser(RegisterUserDTO dto) {
        // Validaciones
        validateUserRegistration(dto);

        // Crear usuario
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    private void validateUserRegistration(RegisterUserDTO dto) {
        // Campos mandatorios
        if (dto.getFirstName() == null || dto.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is mandatory");
        }
        if (dto.getLastName() == null || dto.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is mandatory");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is mandatory");
        }
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is mandatory");
        }

        // Validar email
        if (!EMAIL_PATTERN.matcher(dto.getEmail()).matches()) {
            throw new IllegalArgumentException("Email must be a valid email address");
        }

        // Validar firstName - al menos 1 letra mayúscula A-Z
        if (!dto.getFirstName().matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("First name must contain at least 1 uppercase letter (A-Z)");
        }

        // Validar lastName - al menos 1 letra mayúscula A-Z
        if (!dto.getLastName().matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Last name must contain at least 1 uppercase letter (A-Z)");
        }

        // Validar password - al menos 8 caracteres, con al menos una letra y un número
        if (dto.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if (!dto.getPassword().matches(".*[a-zA-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one letter");
        }
        if (!dto.getPassword().matches(".*[0-9].*")) {
            throw new IllegalArgumentException("Password must contain at least one number");
        }
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public void deleteAll() {
        userRepository.deleteAll();
    }
}
