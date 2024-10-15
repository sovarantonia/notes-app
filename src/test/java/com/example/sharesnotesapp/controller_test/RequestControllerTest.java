package com.example.sharesnotesapp.controller_test;

import com.example.sharesnotesapp.controller.RequestController;
import com.example.sharesnotesapp.model.Request;
import com.example.sharesnotesapp.model.Status;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.mapper.RequestMapper;
import com.example.sharesnotesapp.model.dto.request.RequestRequestDto;
import com.example.sharesnotesapp.model.dto.response.RequestResponseDto;
import com.example.sharesnotesapp.model.dto.response.UserResponseDto;
import com.example.sharesnotesapp.service.request.RequestServiceImpl;
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
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@WebMvcTest(RequestController.class)
@Import(TestSecurityConfig.class)
public class RequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestServiceImpl requestService;

    @MockBean
    private RequestMapper mapper;

    private Authentication authentication;
    private User sender;
    private User receiver;
    private Request request;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        sender = new User(1L, "Sender", "Sender", "sender@example.com", "test123");
        receiver = new User(2L, "Receiver", "Receiver", "receiver@example.com", "test123");

        request = new Request(1L, sender, receiver, Status.PENDING, LocalDateTime.parse("2024-09-09T00:00:00"));

        authentication = new UsernamePasswordAuthenticationToken(sender, sender.getPassword(), Collections.emptyList());
    }

    @Test
    public void testSendRequest() throws Exception {
        UserResponseDto senderResponse
                = new UserResponseDto(sender.getId(), sender.getFirstName(), sender.getLastName(), sender.getEmail());
        UserResponseDto receiverResponse
                = new UserResponseDto(receiver.getId(), receiver.getFirstName(), receiver.getLastName(), receiver.getEmail());
        RequestResponseDto responseDto
                = new RequestResponseDto(senderResponse, receiverResponse, request.getStatus(), request.getSentAt());

        when(mapper.toDto(any(Request.class))).thenReturn(responseDto);
        when(requestService.sendRequest(any(RequestRequestDto.class))).thenReturn(request);
        String requestBody = "{ \"senderId\": 1, \"receiverEmail\": \"receiver@example.com\"}";

        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sender.id", is(1)))
                .andExpect(jsonPath("$.sender.firstName", is("Sender")))
                .andExpect(jsonPath("$.sender.lastName", is("Sender")))
                .andExpect(jsonPath("$.sender.email", is("sender@example.com")))
                .andExpect(jsonPath("$.receiver.id", is(2)))
                .andExpect(jsonPath("$.receiver.firstName", is("Receiver")))
                .andExpect(jsonPath("$.receiver.lastName", is("Receiver")))
                .andExpect(jsonPath("$.receiver.email", is("receiver@example.com")))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.sentAt", is("09-09-2024 00:00:00")));
    }

    @Test
    public void testSendRequest_UserNotLoggedIn() throws Exception{
        String requestBody = "{ \"senderId\": 1, \"receiverEmail\": \"receiver@example.com\"}";

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSendRequest_SameUser() throws Exception{
        String requestBody = "{ \"senderId\": 1, \"receiverEmail\": \"sender@example.com\"}";

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(requestService.sendRequest(any(RequestRequestDto.class)))
                .thenThrow(new IllegalArgumentException("Cannot send a request to yourself"));

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot send a request to yourself"));
    }

    @Test
    public void testSendRequest_InvalidUserId() throws Exception{
        String requestBody = "{ \"senderId\": 1000, \"receiverEmail\": \"receiver@example.com\"}";

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(requestService.sendRequest(any(RequestRequestDto.class)))
                .thenThrow(new EntityNotFoundException("User does not exist"));

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User does not exist"));
    }

    @Test
    public void testSendRequest_InvalidUserEmail() throws Exception{
        String requestBody = "{ \"senderId\": 1, \"receiverEmail\": \"sr@example.com\"}";

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(requestService.sendRequest(any(RequestRequestDto.class)))
                .thenThrow(new EntityNotFoundException("User does not exist"));

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User does not exist"));
    }

    @Test
    public void testSendRequest_AlreadyPendingRequest() throws Exception{
        String requestBody = "{ \"senderId\": 1, \"receiverEmail\": \"receiver@example.com\"}";

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(requestService.sendRequest(any(RequestRequestDto.class)))
                .thenThrow(new IllegalArgumentException("There is already a request created"));

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("There is already a request created"));
    }

    @Test
    public void testSendRequest_AlreadyAcceptedRequest() throws Exception{
        String requestBody = "{ \"senderId\": 1, \"receiverEmail\": \"receiver@example.com\"}";

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(requestService.sendRequest(any(RequestRequestDto.class)))
                .thenThrow(new IllegalArgumentException("Cannot send another request"));

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot send another request"));
    }

    @Test
    public void testDeleteRequest() throws Exception{
        Long id = 1L;
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(delete("/requests/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteRequest_InvalidId() throws Exception{
        Long id = 100L;
        doThrow(new EntityNotFoundException("Request does not exist")).when(requestService).deleteRequest(id);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(delete("/requests/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Request does not exist"));
    }

    @Test
    public void testDeleteRequest_UserNotLoggedIn() throws Exception{
        Long id = 100L;
        mockMvc.perform(delete("/requests/{id}", id))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteRequest_NonPendingRequest() throws Exception{
        Long id = 2L;
        doThrow(new IllegalArgumentException("Cannot delete a non-pending request")).when(requestService).deleteRequest(id);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(delete("/requests/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot delete a non-pending request"));
    }

    @Test
    public void testAcceptRequest() throws Exception{
        Long id = 1L;
        UserResponseDto senderResponse
                = new UserResponseDto(sender.getId(), sender.getFirstName(), sender.getLastName(), sender.getEmail());
        UserResponseDto receiverResponse
                = new UserResponseDto(receiver.getId(), receiver.getFirstName(), receiver.getLastName(), receiver.getEmail());

        RequestResponseDto responseDto
                = new RequestResponseDto(senderResponse, receiverResponse, Status.ACCEPTED, request.getSentAt());

        when(requestService.getRequestById(any(Long.class))).thenReturn(request);
        when(mapper.toDto(any(Request.class))).thenReturn(responseDto);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(patch("/requests/{id}/accept", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ACCEPTED")));
    }

    @Test
    public void testAcceptRequest_UserNotLoggedIn() throws Exception{
        Long id = 1L;

        mockMvc.perform(patch("/requests/{id}/accept", id))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAcceptRequest_NonPendingRequest() throws Exception{
        Long id = 1L;
        doThrow(new IllegalArgumentException("Cannot accept a non-pending request")).when(requestService).acceptRequest(id);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(patch("/requests/{id}/accept", id))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAcceptRequest_InvalidId() throws Exception{
        Long id = 1000L;
        doThrow(new EntityNotFoundException("Request does not exist")).when(requestService).acceptRequest(id);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(patch("/requests/{id}/accept", id))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeclineRequest() throws Exception{
        Long id = 1L;
        UserResponseDto senderResponse
                = new UserResponseDto(sender.getId(), sender.getFirstName(), sender.getLastName(), sender.getEmail());
        UserResponseDto receiverResponse
                = new UserResponseDto(receiver.getId(), receiver.getFirstName(), receiver.getLastName(), receiver.getEmail());
        RequestResponseDto responseDto
                = new RequestResponseDto(senderResponse, receiverResponse, Status.DECLINED, request.getSentAt());

        when(requestService.getRequestById(any(Long.class))).thenReturn(request);
        when(mapper.toDto(any(Request.class))).thenReturn(responseDto);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(patch("/requests/{id}/decline", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("DECLINED")));
    }

    @Test
    public void testDeclineRequest_UserNotLoggedIn() throws Exception{
        Long id = 1L;

        mockMvc.perform(patch("/requests/{id}/decline", id))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeclineRequest_NonPendingRequest() throws Exception{
        Long id = 1L;
        doThrow(new IllegalArgumentException("Cannot decline a non-pending request")).when(requestService).declineRequest(id);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(patch("/requests/{id}/decline", id))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeclineRequest_InvalidId() throws Exception{
        Long id = 1000L;
        doThrow(new EntityNotFoundException("Request does not exist")).when(requestService).declineRequest(id);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(patch("/requests/{id}/decline", id))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetSentRequests() throws Exception{
        User user = new User(3L, "User", "User", "user@example.com", "test123");
        Request request1 = new Request(2L, sender, user, Status.PENDING, LocalDateTime.parse("2024-10-11T12:00:10"));

        UserResponseDto senderResponse
                = new UserResponseDto(sender.getId(), sender.getFirstName(), sender.getLastName(), sender.getEmail());
        UserResponseDto receiverResponse
                = new UserResponseDto(receiver.getId(), receiver.getFirstName(), receiver.getLastName(), receiver.getEmail());
        UserResponseDto userResponse
                = new UserResponseDto(3L, "User", "User", "user@example.com");

        RequestResponseDto responseDto
                = new RequestResponseDto(senderResponse, receiverResponse, request.getStatus(), request.getSentAt());
        RequestResponseDto responseDto1
                = new RequestResponseDto(senderResponse, userResponse, request.getStatus(), LocalDateTime.parse("2024-10-11T12:00:10"));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(requestService.getSentRequests(any(User.class))).thenReturn(List.of(request1, request));
        when(mapper.toDto(request)).thenReturn(responseDto);
        when(mapper.toDto(request1)).thenReturn(responseDto1);

        mockMvc.perform(get("/requests/sent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sender.id", is(1)))
                .andExpect(jsonPath("$[0].sender.firstName", is("Sender")))
                .andExpect(jsonPath("$[0].sender.lastName", is("Sender")))
                .andExpect(jsonPath("$[0].sender.email", is("sender@example.com")))
                .andExpect(jsonPath("$[0].receiver.id", is(3)))
                .andExpect(jsonPath("$[0].receiver.firstName", is("User")))
                .andExpect(jsonPath("$[0].receiver.lastName", is("User")))
                .andExpect(jsonPath("$[0].receiver.email", is("user@example.com")))
                .andExpect(jsonPath("$[0].status", is("PENDING")))
                .andExpect(jsonPath("$[0].sentAt", is("11-10-2024 12:00:10")))
                .andExpect(jsonPath("$[1].sender.id", is(1)))
                .andExpect(jsonPath("$[1].sender.firstName", is("Sender")))
                .andExpect(jsonPath("$[1].sender.lastName", is("Sender")))
                .andExpect(jsonPath("$[1].sender.email", is("sender@example.com")))
                .andExpect(jsonPath("$[1].receiver.id", is(2)))
                .andExpect(jsonPath("$[1].receiver.firstName", is("Receiver")))
                .andExpect(jsonPath("$[1].receiver.lastName", is("Receiver")))
                .andExpect(jsonPath("$[1].receiver.email", is("receiver@example.com")))
                .andExpect(jsonPath("$[1].status", is("PENDING")))
                .andExpect(jsonPath("$[1].sentAt", is("09-09-2024 00:00:00")));;
    }

    @Test
    public void testGetSentRequests_UserNotLoggedIn() throws Exception{
        mockMvc.perform(get("/requests/sent"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetReceivedRequests() throws Exception{
        User user = new User(3L, "User", "User", "user@example.com", "test123");
        Request request1 = new Request(2L, user, sender, Status.PENDING, LocalDateTime.parse("2024-10-11T12:00:10"));
        request.setSender(receiver);
        request.setReceiver(sender);

        UserResponseDto senderResponse
                = new UserResponseDto(sender.getId(), sender.getFirstName(), sender.getLastName(), sender.getEmail());
        UserResponseDto receiverResponse
                = new UserResponseDto(receiver.getId(), receiver.getFirstName(), receiver.getLastName(), receiver.getEmail());
        UserResponseDto userResponse
                = new UserResponseDto(3L, "User", "User", "user@example.com");

        RequestResponseDto responseDto
                = new RequestResponseDto(receiverResponse, senderResponse, request.getStatus(), request.getSentAt());
        RequestResponseDto responseDto1
                = new RequestResponseDto(userResponse, senderResponse, request.getStatus(), LocalDateTime.parse("2024-10-11T12:00:10"));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(requestService.getReceivedRequests(any(User.class))).thenReturn(List.of(request1, request));
        when(mapper.toDto(request)).thenReturn(responseDto);
        when(mapper.toDto(request1)).thenReturn(responseDto1);

        mockMvc.perform(get("/requests/received"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sender.id", is(3)))
                .andExpect(jsonPath("$[0].sender.firstName", is("User")))
                .andExpect(jsonPath("$[0].sender.lastName", is("User")))
                .andExpect(jsonPath("$[0].sender.email", is("user@example.com")))
                .andExpect(jsonPath("$[0].receiver.id", is(1)))
                .andExpect(jsonPath("$[0].receiver.firstName", is("Sender")))
                .andExpect(jsonPath("$[0].receiver.lastName", is("Sender")))
                .andExpect(jsonPath("$[0].receiver.email", is("sender@example.com")))
                .andExpect(jsonPath("$[0].status", is("PENDING")))
                .andExpect(jsonPath("$[0].sentAt", is("11-10-2024 12:00:10")))
                .andExpect(jsonPath("$[1].sender.id", is(2)))
                .andExpect(jsonPath("$[1].sender.firstName", is("Receiver")))
                .andExpect(jsonPath("$[1].sender.lastName", is("Receiver")))
                .andExpect(jsonPath("$[1].sender.email", is("receiver@example.com")))
                .andExpect(jsonPath("$[1].receiver.id", is(1)))
                .andExpect(jsonPath("$[1].receiver.firstName", is("Sender")))
                .andExpect(jsonPath("$[1].receiver.lastName", is("Sender")))
                .andExpect(jsonPath("$[1].receiver.email", is("sender@example.com")))
                .andExpect(jsonPath("$[1].status", is("PENDING")))
                .andExpect(jsonPath("$[1].sentAt", is("09-09-2024 00:00:00")));;
    }

    @Test
    public void testGetReceivedRequests_UserNotLoggedIn() throws Exception{
        mockMvc.perform(get("/requests/received"))
                .andExpect(status().isBadRequest());
    }

//    @Test
//    public void testRemoveFromFriendList() throws Exception{
//        sender = mock(User.class);
//        List<User> friends = new ArrayList<>();
//        friends.add(receiver);
//
//        when(sender.getFriendList()).thenReturn(friends);
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        mockMvc.perform(delete("/requests/remove-friend/{friendId}", ))
//    }

}
