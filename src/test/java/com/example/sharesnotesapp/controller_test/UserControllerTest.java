package com.example.sharesnotesapp.controller_test;

import com.example.sharesnotesapp.controller.UserController;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.mapper.UserMapper;
import com.example.sharesnotesapp.model.dto.request.UserRequestDto;
import com.example.sharesnotesapp.model.dto.response.UserResponseDto;
import com.example.sharesnotesapp.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

import java.util.Collections;
import java.util.Optional;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private UserMapper mapper;
    private Authentication authentication;
    private User user;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setFirstName("First-name");
        user.setLastName("Last-name");
        user.setPassword("test123");
        user.setEmail("email@test.com");

        authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), Collections.emptyList());
    }

    @Test
    @WithMockUser(username = "email@test.com", password = "test123")
    void testGetUserById() throws Exception {
        Long id = 1L;
        UserResponseDto userResponseDto = new UserResponseDto(user.getFirstName(), user.getLastName(), user.getEmail());

        when(userService.getUserById(id)).thenReturn(Optional.of(user));
        when(mapper.toDto(user)).thenReturn(userResponseDto);

        mockMvc.perform(get("/user/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", is("First-name")))
                .andExpect(jsonPath("$.lastName", is("Last-name")))
                .andExpect(jsonPath("$.email", is("email@test.com")));
    }

    @Test
    void testGetUserById_InvalidId() throws Exception {
        Long nonExistentId = 999L;

        when(userService.getUserById(nonExistentId))
                .thenThrow(new EntityNotFoundException(String.format("User with id %s does not exist", nonExistentId)));

        mockMvc.perform(get("/user/{id}", nonExistentId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format("User with id %s does not exist", nonExistentId)));
    }

    @Test
    void testDeleteUser() throws Exception {
        Long id = 1L;
        when(userService.getUserById(id)).thenReturn(Optional.of(user));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(delete("/user/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUser_InvalidId() throws Exception {
        Long nonExistentId = 999L;

        when(userService.getUserById(nonExistentId))
                .thenThrow(new EntityNotFoundException(("User with does not exist")));

        mockMvc.perform(delete("/user/{id}", nonExistentId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteUser_UserNotLoggedIn() throws Exception {
        Long id = 1L;

        when(userService.getUserById(id)).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/user/{id}", id))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateCredentials() throws Exception {
        Long id = 1L;
        String requestBody
                = "{ \"firstName\": \"New-name\", \"lastName\": \"Last-name\", \"email\": \"email@test.com\", \"password\": \"test123\" }";
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName(user.getFirstName());
        userRequestDto.setLastName(user.getLastName());
        userRequestDto.setEmail(user.getEmail());
        userRequestDto.setPassword(user.getPassword());
        UserResponseDto userResponseDto = new UserResponseDto("New-name", user.getLastName(), user.getEmail());

        when(userService.getUserById(id)).thenReturn(Optional.of(user));
        when(mapper.toDto(userService.updateUserCredentials(id, userRequestDto))).thenReturn(userResponseDto);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(patch("/user/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("New-name")))
                .andExpect(jsonPath("$.lastName", is("Last-name")))
                .andExpect(jsonPath("$.email", is("email@test.com")));
    }

    @Test
    void testUpdateCredentials_InvalidId() throws Exception {
        Long id = 999L;
        String requestBody
                = "{ \"firstName\": \"New-name\", \"lastName\": \"Last-name\", \"email\": \"email@test.com\", \"password\": \"test123\" }";
        UserRequestDto userRequestDto = new UserRequestDto();

        when(userService.getUserById(id)).thenReturn(Optional.empty());
        when(userService.updateUserCredentials(id, userRequestDto)).thenThrow(new UsernameNotFoundException("No user with that username"));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(patch("/user/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateCredentials_InvalidCredentials() throws Exception{
        Long id = 1L;
        String requestBody
                = "{ \"firstName\": \"\", \"lastName\": \"Last-name\", \"email\": \"email@test.com\", \"password\": \"test123\" }";

        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(patch("/user/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.firstName", is("First name should not be empty")));
    }

    @Test
    void testUpdateCredentials_UserNotLoggedIn() throws Exception {
        Long id = 1L;
        String requestBody
                = "{ \"firstName\": \"New-name\", \"lastName\": \"Last-name\", \"email\": \"email@test.com\", \"password\": \"test123\" }";

        when(userService.getUserById(id)).thenReturn(Optional.of(user));

        mockMvc.perform(patch("/user/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isBadRequest());

    }
}
