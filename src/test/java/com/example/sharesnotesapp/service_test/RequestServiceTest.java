package com.example.sharesnotesapp.service_test;

import com.example.sharesnotesapp.model.Request;
import com.example.sharesnotesapp.model.Status;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.request.RequestRequestDto;
import com.example.sharesnotesapp.repository.RequestRepository;
import com.example.sharesnotesapp.repository.UserRepository;
import com.example.sharesnotesapp.service.request.RequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class RequestServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private RequestServiceImpl requestService;

    private User sender;
    private User receiver;
    private Request request;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        sender = new User(1L, "Sender", "Sender", "sender@example.com", "test123");
        receiver = new User(2L, "Receiver", "Receiver", "receiver@example.com", "test123");

        request = new Request(1L, sender, receiver, Status.PENDING, LocalDate.now());
    }

    @Test
    public void testSendRequest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findUserByEmail("receiver@example.com")).thenReturn(Optional.of(receiver));
        when(requestRepository.save(any(Request.class))).thenReturn(request);

        RequestRequestDto requestDto = new RequestRequestDto();
        requestDto.setSenderId(1L);
        requestDto.setReceiverEmail("receiver@example.com");

        Request savedRequest = requestService.sendRequest(requestDto);

        assertEquals(request.getSender(), savedRequest.getSender());
        assertEquals(request.getReceiver(), savedRequest.getReceiver());
        assertEquals(request.getSentAt(), savedRequest.getSentAt());
        assertEquals(request.getStatus(), savedRequest.getStatus());
    }

    @Test
    public void testSendRequest_SameUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findUserByEmail("sender@example.com")).thenReturn(Optional.of(sender));

        RequestRequestDto requestDto = new RequestRequestDto();
        requestDto.setSenderId(1L);
        requestDto.setReceiverEmail("sender@example.com");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> requestService.sendRequest(requestDto));
        String message = "Cannot send a request to yourself";
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testSendRequest_InvalidSenderId() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RequestRequestDto requestDto = new RequestRequestDto();
        requestDto.setSenderId(1L);
        requestDto.setReceiverEmail("receiver@example.com");

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> requestService.sendRequest(requestDto));
        String message = "User does not exist";
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testSendRequest_InvalidReceiverEmail() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findUserByEmail("receiver@example.com")).thenReturn(Optional.empty());

        RequestRequestDto requestDto = new RequestRequestDto();
        requestDto.setSenderId(1L);
        requestDto.setReceiverEmail("receiver@example.com");

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> requestService.sendRequest(requestDto));
        String message = "User does not exist";
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testSendRequest_PendingRequestAlreadyExists() {
        Request anotherRequest = new Request(1L, sender, receiver, Status.PENDING, LocalDate.parse("2024-09-09"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findUserByEmail("receiver@example.com")).thenReturn(Optional.of(receiver));
        when(requestRepository.getRequestsBySenderAndReceiver(sender, receiver)).thenReturn(List.of(anotherRequest));

        RequestRequestDto requestDto = new RequestRequestDto();
        requestDto.setSenderId(1L);
        requestDto.setReceiverEmail("receiver@example.com");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> requestService.sendRequest(requestDto));
        String message = "There is already a request created";
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testSendRequest_PendingRequestAlreadyExistsFromReceiver() {
        Request anotherRequest = new Request(1L, receiver, sender, Status.PENDING, LocalDate.parse("2024-09-09"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findUserByEmail("receiver@example.com")).thenReturn(Optional.of(receiver));
        when(requestRepository.getRequestsBySenderAndReceiver(receiver, sender)).thenReturn(List.of(anotherRequest));

        RequestRequestDto requestDto = new RequestRequestDto();
        requestDto.setSenderId(1L);
        requestDto.setReceiverEmail("receiver@example.com");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> requestService.sendRequest(requestDto));
        String message = "There is already a request created";
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testSendRequest_AcceptedRequestAlreadyExists() {
        Request anotherRequest = new Request(1L, sender, receiver, Status.ACCEPTED, LocalDate.parse("2024-09-09"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findUserByEmail("receiver@example.com")).thenReturn(Optional.of(receiver));
        when(requestRepository.getRequestsBySenderAndReceiver(sender, receiver)).thenReturn(List.of(anotherRequest));

        RequestRequestDto requestDto = new RequestRequestDto();
        requestDto.setSenderId(1L);
        requestDto.setReceiverEmail("receiver@example.com");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> requestService.sendRequest(requestDto));
        String message = "Cannot send another request";
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testSendRequest_AcceptedRequestAlreadyExistsFromReceiver() {
        Request anotherRequest = new Request(1L, receiver, sender, Status.ACCEPTED, LocalDate.parse("2024-09-09"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findUserByEmail("receiver@example.com")).thenReturn(Optional.of(receiver));
        when(requestRepository.getRequestsBySenderAndReceiver(receiver, sender)).thenReturn(List.of(anotherRequest));

        RequestRequestDto requestDto = new RequestRequestDto();
        requestDto.setSenderId(1L);
        requestDto.setReceiverEmail("receiver@example.com");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> requestService.sendRequest(requestDto));
        String message = "Cannot send another request";
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testDeleteRequest() {
        Long id = 1L;
        when(requestRepository.findById(id)).thenReturn(Optional.of(request));
        requestService.deleteRequest(id);
        verify(requestRepository, times(1)).deleteById(id);
    }

    @Test
    public void testDeleteRequest_InvalidId() {
        Long nonExistentId = 999L;
        when(requestRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception
                = assertThrows(EntityNotFoundException.class, () -> requestService.deleteRequest(nonExistentId));
        String message = "Request does not exist";
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testDeleteRequest_NonPendingRequest() {
        Long id = 2L;
        Request anotherRequest = new Request(2L, receiver, sender, Status.ACCEPTED, LocalDate.parse("2024-09-09"));
        when(requestRepository.findById(id)).thenReturn(Optional.of(anotherRequest));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> requestService.deleteRequest(id));
        String message = "Cannot delete a non-pending request";
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testAcceptRequest() {
        Long id = 1L;
        when(requestRepository.findById(id)).thenReturn(Optional.of(request));
        requestService.acceptRequest(id);
        assertEquals(Status.ACCEPTED, request.getStatus());
    }

    @Test
    public void testAcceptRequest_InvalidId() {
        Long nonExistentId = 999L;
        when(requestRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception
                = assertThrows(EntityNotFoundException.class, () -> requestService.acceptRequest(nonExistentId));
        String message = "Request does not exist";
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testAcceptRequest_NonPendingRequest(){
        Long id = 2L;
        Request anotherRequest = new Request(2L, receiver, sender, Status.ACCEPTED, LocalDate.parse("2024-09-09"));
        when(requestRepository.findById(id)).thenReturn(Optional.of(anotherRequest));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> requestService.acceptRequest(id));
        String message = "Cannot accept a non-pending request";
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testDeclineRequest() {
        Long id = 1L;
        when(requestRepository.findById(id)).thenReturn(Optional.of(request));
        requestService.declineRequest(id);
        assertEquals(Status.DECLINED, request.getStatus());
    }

    @Test
    public void testDeclineRequest_InvalidId() {
        Long nonExistentId = 999L;
        when(requestRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception
                = assertThrows(EntityNotFoundException.class, () -> requestService.declineRequest(nonExistentId));
        String message = "Request does not exist";
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testDeclineRequest_NonPendingRequest(){
        Long id = 2L;
        Request anotherRequest = new Request(2L, receiver, sender, Status.ACCEPTED, LocalDate.parse("2024-09-09"));
        when(requestRepository.findById(id)).thenReturn(Optional.of(anotherRequest));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> requestService.declineRequest(id));
        String message = "Cannot decline a non-pending request";
        assertEquals(message, exception.getMessage());
    }
}
