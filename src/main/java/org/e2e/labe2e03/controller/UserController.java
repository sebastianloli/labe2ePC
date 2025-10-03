package org.e2e.labe2e03.controller;

import lombok.RequiredArgsConstructor;
import org.e2e.labe2e03.dto.request.RegisterUserDTO;
import org.e2e.labe2e03.dto.response.NewIdDTO;
import org.e2e.labe2e03.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<NewIdDTO> register(@RequestBody RegisterUserDTO dto) {
        Long userId = userService.registerUser(dto);
        return ResponseEntity.ok(new NewIdDTO(String.valueOf(userId)));
    }
}
