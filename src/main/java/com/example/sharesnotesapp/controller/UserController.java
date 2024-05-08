package com.example.sharesnotesapp.controller;

import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.mapper.UserMapper;
import com.example.sharesnotesapp.model.dto.request.UserRequestDto;
import com.example.sharesnotesapp.model.dto.response.UserResponseDto;
import com.example.sharesnotesapp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final UserService userService;
    @Autowired
    private final UserMapper mapper;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id).orElseThrow(EntityNotFoundException::new);
        return ResponseEntity.ok(mapper.toDto(user));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateCredentials(@PathVariable Long id, @RequestBody UserRequestDto userRequestDto) {
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
