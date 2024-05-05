package com.example.sharesnotesapp.service;

import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.request.UserRequestDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {

    Optional<User> getUserById(Long id);
    void validateEmail(String email);

    User saveUser(UserRequestDto userRequestDto);

    void deleteUserByEmail(String email);
    void updateUserCredentials(UserRequestDto userRequestDto);

}
