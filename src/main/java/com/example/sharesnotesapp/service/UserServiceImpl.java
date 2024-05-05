package com.example.sharesnotesapp.service;

import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.request.UserRequestDto;
import com.example.sharesnotesapp.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    //crud, getUser by id, by email

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Checks if there is already a user with the email
     *
     * @param email - the email to be verified
     */
    @Override
    public void validateEmail(String email) {
        if (userRepository.findUserByEmail(email).isPresent()) {
            throw new IllegalArgumentException(String.format("%s is already used", email));
        }
    }

    /**
     * Saves the new user with the credentials from the registration
     *
     * @param userRequestDto - contains the user credentials to be saved
     */

    @Override
    public User saveUser(UserRequestDto userRequestDto) {
        validateEmail(userRequestDto.getEmail());

        final User newUser = User.builder()
                .firstName(userRequestDto.getFirstName())
                .lastName(userRequestDto.getLastName())
                .email(userRequestDto.getEmail())
                .password(userRequestDto.getPassword())
                .build();

        newUser.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));

        return userRepository.save(newUser);
    }


    @Override
    public void deleteUserByEmail(String email) {
        if (userRepository.findUserByEmail(email).isEmpty()) {
            throw new UsernameNotFoundException(String.format("User with the address %s does not exist", email));
        }

        userRepository.delete(userRepository.findUserByEmail(email).get());
    }

    @Override
    public void updateUserCredentials(UserRequestDto userRequestDto) {

    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(username).orElseThrow(() -> new UsernameNotFoundException("No user with that username"));
    }
}
