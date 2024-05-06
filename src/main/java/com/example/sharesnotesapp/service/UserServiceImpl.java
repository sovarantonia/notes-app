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

import javax.persistence.EntityNotFoundException;
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

    @Override
    public Optional<User> getUserByEmail(String email) {
        if (userRepository.findUserByEmail(email).isEmpty()) {
            throw new UsernameNotFoundException(String.format("User with the address %s does not exist", email));
        }

        return userRepository.findUserByEmail(email);
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
    public void deleteUser(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("User does not exist");
        }
        userRepository.deleteById(id);
    }

    @Override
    public User updateUserCredentials(Long id, UserRequestDto userRequestDto) {
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("No user with that username"));

        if (!(userRequestDto.getEmail().isEmpty() || userRequestDto.getEmail().isBlank())) {
            userToUpdate.setEmail(userRequestDto.getEmail());
        }

        if (!(userRequestDto.getFirstName().isEmpty() || userRequestDto.getFirstName().isBlank())) {
            userToUpdate.setFirstName(userRequestDto.getFirstName());
        }

        if (!(userRequestDto.getLastName().isEmpty() || userRequestDto.getLastName().isBlank())) {
            userToUpdate.setLastName(userRequestDto.getLastName());
        }

        if (!(userRequestDto.getPassword().isEmpty() || userRequestDto.getPassword().isBlank())) {
            userToUpdate.setPassword(userRequestDto.getPassword());
        }

        return userRepository.save(userToUpdate);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(username).orElseThrow(() -> new UsernameNotFoundException("No user with that username"));
    }
}
