package com.example.sharesnotesapp.controller;

import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.request.UserRequestDto;
import com.example.sharesnotesapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> createAccount(@Valid @RequestBody UserRequestDto userRequestDto){
        URI uri = URI.create((ServletUriComponentsBuilder.fromCurrentContextPath().path("/register").toUriString()));
        return ResponseEntity.created(uri).body(userService.saveUser(userRequestDto));
    }
}
