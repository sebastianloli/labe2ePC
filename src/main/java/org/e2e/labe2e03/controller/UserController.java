package org.e2e.labe2e03.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.e2e.labe2e03.dto.request.RegisterUserDTO;
import org.e2e.labe2e03.dto.response.NewIdDTO;
import org.e2e.labe2e03.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<NewIdDTO> register(@RequestBody RegisterUserDTO dto) {
        Long userId = userService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new NewIdDTO(String.valueOf(userId)));
    }
}
