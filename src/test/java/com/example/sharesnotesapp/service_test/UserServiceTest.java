package com.example.sharesnotesapp.service_test;

import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.request.UserRequestDto;
import com.example.sharesnotesapp.repository.UserRepository;
import com.example.sharesnotesapp.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveUser() {
        User user = new User();
        user.setFirstName("ExampleA");
        user.setLastName("ExampleB");
        user.setEmail("test_example@test.com");
        user.setPassword(passwordEncoder.encode("test123"));

        UserRequestDto userDto = new UserRequestDto();
        userDto.setFirstName("ExampleA");
        userDto.setLastName("ExampleB");
        userDto.setEmail("test_example@test.com");
        userDto.setPassword("test123");

        when(userRepository.findUserByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        User createdUser = userService.saveUser(userDto);

        assertEquals("ExampleA", createdUser.getFirstName());
        assertEquals("ExampleB", createdUser.getLastName());
        assertEquals("test_example@test.com", createdUser.getEmail());
        assertEquals(passwordEncoder.encode("test123"), createdUser.getPassword());

        verify(userRepository).save(Mockito.any(User.class));
    }

    @Test
    void testSaveUser_EmailAlreadyExists(){
        User existingUser = new User();
        existingUser.setFirstName("Exist");
        existingUser.setLastName("Exist");
        existingUser.setEmail("exists@test.com");
        existingUser.setPassword(passwordEncoder.encode("test123"));

        String existingEmail = "exists@test.com";
        when(userRepository.findUserByEmail(existingEmail)).thenReturn(Optional.of(existingUser));

        UserRequestDto userDto = new UserRequestDto();
        userDto.setFirstName("Exists");
        userDto.setLastName("Exists");
        userDto.setEmail("exists@test.com");
        userDto.setPassword("test123");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.saveUser(userDto);
        });

        String message = String.format("%s is already used", existingEmail);
        assertEquals(message, exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserById(){
        User user = new User();
        user.setId(1L);
        user.setFirstName("ExampleA");
        user.setLastName("ExampleB");
        user.setEmail("test_example@test.com");
        user.setPassword(passwordEncoder.encode("test123"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(1L).orElseThrow(null);

        assertEquals("ExampleA", foundUser.getFirstName());
        assertEquals("ExampleB", foundUser.getLastName());
        assertEquals("test_example@test.com", foundUser.getEmail());
        assertEquals(passwordEncoder.encode("test123"), foundUser.getPassword());
    }

    @Test
    void testGetUserById_NonExistent(){
        Long nonExistentId = 888L;

        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(EntityNotFoundException.class, () -> {userService.getUserById(nonExistentId);});

        String message = String.format("User with id %s does not exist", nonExistentId);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testGetUserByEmail(){
        User user = new User();
        user.setId(1L);
        user.setFirstName("ExampleA");
        user.setLastName("ExampleB");
        user.setEmail("test_example@test.com");
        user.setPassword(passwordEncoder.encode("test123"));

        String email = "test_example@test.com";

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserByEmail(email).orElseThrow(null);

        assertEquals("ExampleA", foundUser.getFirstName());
        assertEquals("ExampleB", foundUser.getLastName());
        assertEquals("test_example@test.com", foundUser.getEmail());
        assertEquals(passwordEncoder.encode("test123"), foundUser.getPassword());
    }

    @Test
    void testGetUserByEmail_NonExistent(){
        String nonExistentEmail = "no_email@test.com";

        when(userRepository.findUserByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(UsernameNotFoundException.class, () -> {userService.getUserByEmail(nonExistentEmail);});

        String message = String.format("User with the address %s does not exist", nonExistentEmail);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testValidateEmail(){
        String newValidEmail = "new_email@test.com";

        when(userRepository.findUserByEmail(newValidEmail)).thenReturn(Optional.empty());

        userService.validateEmail(newValidEmail);
    }

    @Test
    void testValidateEmail_NotValid(){
        User user = new User();
        String alreadyUsedEmail = "used_email@test.com";
        user.setFirstName("A");
        user.setLastName("B");
        user.setEmail(alreadyUsedEmail);

        when(userRepository.findUserByEmail(alreadyUsedEmail)).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.validateEmail(alreadyUsedEmail));

        String message = alreadyUsedEmail + " is already used";
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testDeleteUser(){
        Long id = 1L;
        User user = new User();
        user.setId(id);
        user.setFirstName("ExampleA");
        user.setLastName("ExampleB");
        user.setEmail("test_example@test.com");
        user.setPassword(passwordEncoder.encode("test123"));

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.deleteUser(id);

        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteUser_NonExistentId(){
        Long nonExistentId = 999L;

        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(nonExistentId));

        String message = "User does not exist";
        assertEquals(message, exception.getMessage());

        verify(userRepository, never()).deleteById(nonExistentId);
    }

    @Test
    void testUpdateUserCredentials(){
        User user = new User();
        Long id = 1L;
        user.setId(id);
        user.setFirstName("ExampleA");
        user.setLastName("ExampleB");
        user.setEmail("test_example@test.com");
        user.setPassword(passwordEncoder.encode("test123"));

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName("New-Name");
        userService.updateUserCredentials(id, userRequestDto);

        assertEquals(userRequestDto.getFirstName(), user.getFirstName());
        assertEquals("ExampleB", user.getLastName());
        assertEquals("test_example@test.com", user.getEmail());
        assertEquals(passwordEncoder.encode("test123"), user.getPassword());
    }

    @Test
    void testUpdateUserCredential_InvalidEmail(){
        User user = new User();
        String existingEmail = "existing_email@test.com";
        Long id = 1L;
        user.setId(id);
        user.setFirstName("ExampleA");
        user.setLastName("ExampleB");
        user.setEmail(existingEmail);
        user.setPassword(passwordEncoder.encode("test123"));

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.findUserByEmail(existingEmail)).thenReturn(Optional.of(user));

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setEmail(existingEmail);
        IllegalArgumentException exception = assertThrows
                (IllegalArgumentException.class, () -> userService.updateUserCredentials(id, userRequestDto));

        String message = existingEmail + " is already used";

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testUpdateUserCredential_InvalidId(){
        Long nonExistingId = 123L;

        when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName("Name");

        UsernameNotFoundException exception = assertThrows
                (UsernameNotFoundException.class, () -> userService.updateUserCredentials(nonExistingId, userRequestDto));

        String message = "No user with that username";

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testUpdateUserCredential_AllEmptyFields(){
        User user = new User();
        Long id = 1L;
        user.setId(id);
        user.setFirstName("ExampleA");
        user.setLastName("ExampleB");
        user.setEmail("email@email.com");
        user.setPassword(passwordEncoder.encode("test123"));

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserRequestDto userRequestDto = new UserRequestDto();

        userService.updateUserCredentials(id, userRequestDto);

        assertEquals("ExampleA", user.getFirstName());
        assertEquals("ExampleB", user.getLastName());
        assertEquals("email@email.com", user.getEmail());
        assertEquals(passwordEncoder.encode("test123"), user.getPassword());
    }


}
