package com.example.banking_application_spring_boot.controller;

import com.example.banking_application_spring_boot.dto.RegisterUserRequest;
import com.example.banking_application_spring_boot.entity.Role;
import com.example.banking_application_spring_boot.dto.UserResponse;
import com.example.banking_application_spring_boot.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody RegisterUserRequest registerUserRequest) {
        registerUserRequest.setRole(Role.USER);
        UserResponse response = userService.registerUser(registerUserRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/create")
    @PreAuthorize("hasAuthority('DEMO_WRITE')")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> registerByAdmin(@RequestBody RegisterUserRequest registerUserRequest) {
        UserResponse response = userService.registerUser(registerUserRequest);
        return ResponseEntity.ok(response);
    }
}
