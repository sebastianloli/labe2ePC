package org.e2e.labe2e03.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.e2e.labe2e03.dto.request.LoginDTO;
import org.e2e.labe2e03.dto.response.AuthTokenDTO;
import org.e2e.labe2e03.entity.User;
import org.e2e.labe2e03.service.CustomUserDetailsService;
import org.e2e.labe2e03.security.JwtUtil;
import org.e2e.labe2e03.service.UserService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthTokenDTO> login(@RequestBody LoginDTO dto) {
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is mandatory");
        }
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is mandatory");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );
        } catch (BadCredentialsException e) {
            if (!userService.findByEmail(dto.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Unknown email");
            }
            throw new IllegalArgumentException("Wrong password");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getEmail());
        User user = userService.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        final String jwt = jwtUtil.generateToken(userDetails.getUsername(), user.getId());

        return ResponseEntity.ok(new AuthTokenDTO(jwt));
    }
}
