package com.example.sharesnotesapp.controller;

import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.mapper.UserMapper;
import com.example.sharesnotesapp.model.dto.request.UserRequestDto;
import com.example.sharesnotesapp.model.dto.response.UserResponseDto;
import com.example.sharesnotesapp.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;


@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;

    @Autowired
    public UserController(UserService userService, UserMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id).orElseThrow(EntityNotFoundException::new);
        return ResponseEntity.ok(mapper.toDto(user));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateCredentials(@PathVariable Long id, @Valid @RequestBody UserRequestDto userRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            User updatedUser = userService.updateUserCredentials(id, userRequestDto);
            return ResponseEntity.ok(mapper.toDto(updatedUser));
        }

        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }
}
