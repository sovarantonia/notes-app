package com.example.sharesnotesapp.controller_test;

import com.example.sharesnotesapp.controller.AuthController;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.mapper.UserMapper;
import com.example.sharesnotesapp.model.dto.request.UserRequestDto;
import com.example.sharesnotesapp.model.dto.response.UserResponseDto;
import com.example.sharesnotesapp.security.jwt.JwtUtils;
import com.example.sharesnotesapp.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserMapper mapper;
    private User user;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setFirstName("First-name");
        user.setLastName("Last-name");
        user.setPassword("test123");
        user.setEmail("email@test.com");
    }

    @Test
    void testCreateAccount() throws Exception {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName("First-name");
        userRequestDto.setLastName("Last-name");
        userRequestDto.setPassword("test123");
        userRequestDto.setEmail("email@test.com");

        String registerJson =
                "{ \"firstName\": \"First-name\", \"lastName\": \"Last-name\", \"email\": \"email@test.com\", \"password\": \"test123\" }";

        when(userService.saveUser(any(UserRequestDto.class))).thenReturn(user);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("First-name")))
                .andExpect(jsonPath("$.lastName", is("Last-name")))
                .andExpect(jsonPath("$.email", is("email@test.com")));
    }

    @Test
    void testCreateAccount_EmailAlreadyUsed() throws Exception {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName("First-name");
        userRequestDto.setLastName("Last-name");
        userRequestDto.setPassword("test123");
        userRequestDto.setEmail("email@test.com");

        String registerJson =
                "{ \"firstName\": \"First-name\", \"lastName\": \"Last-name\", \"email\": \"email@test.com\", \"password\": \"test123\" }";

        when(userService.saveUser(any(UserRequestDto.class)))
                .thenThrow(new IllegalArgumentException(String.format("%s is already used", user.getEmail())));

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format("%s is already used", user.getEmail())));
    }

    @Test
    void testCreateAccount_InvalidCredentials() throws Exception {
        String registerJson =
                "{ \"firstName\": \"First-name\", \"lastName\": \"Last-name\", \"email\": \"@test\", \"password\": \"t123\" }";

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email", is("Invalid email format")))
                .andExpect(jsonPath("$.password", is("Password must have at least 7 characters")));
    }


    @Test
    void testLogin() throws Exception {
        String email = "email@test.com";
        String password = "test123";
        String token = "my-token";
        String loginJson = "{ \"email\": \"email@test.com\", \"password\": \"test123\"}";

        UserResponseDto userResponseDto = new UserResponseDto(1L, "a", "b", "email@test.com");


        Authentication authentication = new UsernamePasswordAuthenticationToken(user, password, Collections.emptyList());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(jwtUtils.generateJwtCookie(any(User.class))).thenReturn(token);

        when(mapper.toDto(user)).thenReturn(userResponseDto);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userInfo.email", is(email)))
                .andExpect(jsonPath("$.userInfo.firstName", is("a")))
                .andExpect(jsonPath("$.userInfo.lastName", is("b")))
                .andExpect(jsonPath("$.tokenValue", is(token)));
    }

    @Test
    void testLogin_BadCredentials() throws Exception {
        String loginJson = "{ \"email\": \"email@test.com\", \"password\": \"password\"}";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized());
    }


}
