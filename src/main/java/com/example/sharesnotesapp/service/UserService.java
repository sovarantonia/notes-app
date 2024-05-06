package com.example.sharesnotesapp.service;

import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.request.UserRequestDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {

    Optional<User> getUserById(Long id);

    Optional<User> getUserByEmail(String email);

    void validateEmail(String email);

    User saveUser(UserRequestDto userRequestDto);

    void deleteUser(Long id);

    User updateUserCredentials(Long id, UserRequestDto userRequestDto);

}
