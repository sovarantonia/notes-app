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

import java.time.LocalDateTime;
import java.util.ArrayList;
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

        request = new Request(1L, sender, receiver, Status.PENDING, LocalDateTime.now());

        sender.setFriendList(new ArrayList<>());
        receiver.setFriendList(new ArrayList<>());
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
        Request anotherRequest = new Request(1L, sender, receiver, Status.PENDING, LocalDateTime.parse("2024-09-09T00:00:00"));
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
        Request anotherRequest = new Request(1L, receiver, sender, Status.PENDING, LocalDateTime.parse("2024-09-09T00:00:00"));
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
        Request anotherRequest = new Request(1L, sender, receiver, Status.ACCEPTED, LocalDateTime.parse("2024-09-09T00:00:00"));
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
        Request anotherRequest = new Request(1L, receiver, sender, Status.ACCEPTED, LocalDateTime.parse("2024-09-09T00:00:00"));
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
        Request anotherRequest = new Request(2L, receiver, sender, Status.ACCEPTED, LocalDateTime.parse("2024-09-09T00:00:00"));
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
        Request anotherRequest = new Request(2L, receiver, sender, Status.ACCEPTED, LocalDateTime.parse("2024-09-09T00:00:00"));
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
        Request anotherRequest = new Request(2L, receiver, sender, Status.ACCEPTED, LocalDateTime.parse("2024-09-09T00:00:00"));
        when(requestRepository.findById(id)).thenReturn(Optional.of(anotherRequest));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> requestService.declineRequest(id));
        String message = "Cannot decline a non-pending request";
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testGetSentRequests(){
        User anotherUser = new User(3L, "User", "user", "user@example.com", "test123");
        Request anotherRequest = new Request(2L, sender, anotherUser, Status.PENDING, LocalDateTime.parse("2024-09-09T00:00:00"));

        when(requestRepository.getRequestsBySenderAndStatusOrderBySentAtDesc(sender, Status.PENDING))
                .thenReturn(List.of(request, anotherRequest));

        List<Request> sentRequests = requestService.getSentRequests(sender);
        assertEquals(request, sentRequests.get(0));
        assertEquals(anotherRequest, sentRequests.get(1));
    }

    @Test
    public void testGetReceivedRequests(){
        User anotherUser = new User(3L, "User", "user", "user@example.com", "test123");
        Request anotherRequest = new Request(2L, anotherUser, receiver, Status.PENDING, LocalDateTime.parse("2024-09-09T00:00:00"));

        when(requestRepository.getRequestsByReceiverAndStatusOrderBySentAtDesc(receiver, Status.PENDING))
                .thenReturn(List.of(request, anotherRequest));

        List<Request> sentRequests = requestService.getReceivedRequests(receiver);
        assertEquals(request, sentRequests.get(0));
        assertEquals(anotherRequest, sentRequests.get(1));
    }

    @Test
    public void testAddToFriendList(){
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(sender));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(receiver));
        when(requestRepository.getRequestsBySenderAndReceiver(any(User.class), any(User.class))).thenReturn(List.of(request));
        requestService.addToFriendList(sender, receiver);
        assertEquals(sender, receiver.getFriendList().get(0));
        assertEquals(receiver, sender.getFriendList().get(0));
    }

    @Test
    public void testAddToFriendList_UsersAlreadyFriend(){
        sender = mock(User.class);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(receiver));
        when(sender.getFriendList()).thenReturn(List.of(receiver));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> requestService.addToFriendList(sender, receiver));
        String message = "Users are already friends";
        assertEquals(message, exception.getMessage());
    }
    @Test
    public void testAddToFriendList_SameUser(){
        sender = mock(User.class);
        receiver = mock(User.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(receiver));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(sender.getId()).thenReturn(1L);
        when(receiver.getId()).thenReturn(1L);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> requestService.addToFriendList(sender, receiver));
        String message = "Cannot add yourself to friend list";
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testRemoveFromFriendList() {
        sender.getFriendList().add(receiver);
        receiver.getFriendList().add(sender);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(requestRepository.getRequestsBySenderAndReceiver(sender, receiver)).thenReturn(List.of(request));

        requestService.removeFromFriendList(sender, 2L);
        assertEquals(0, sender.getFriendList().size());
        assertEquals(0, receiver.getFriendList().size());
        verify(userRepository, times(2)).save(any(User.class));
        verify(requestRepository, times(1)).deleteAll(any(List.class));
    }

    @Test
    public void testRemoveFromFriendList_NotFriends() {
        User sender = mock(User.class);
        List<User> friends = new ArrayList<>();

        when(userRepository.findById(any())).thenReturn(Optional.of(sender));
        when(sender.getFriendList()).thenReturn(friends);
        EntityNotFoundException exception
                = assertThrows(EntityNotFoundException.class, () -> requestService.removeFromFriendList(sender, 1L));
        String message = "Users must be friends to remove from friend list";
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testRemoveFromFriendList_SameUser() {
        sender.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        IllegalArgumentException exception
                = assertThrows(IllegalArgumentException.class, () -> requestService.removeFromFriendList(sender, 1L));
        String message = "Must provide different users";
        assertEquals(message, exception.getMessage());
    }


}
