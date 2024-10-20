package com.example.sharesnotesapp.service.request;

import com.example.sharesnotesapp.model.Request;
import com.example.sharesnotesapp.model.Status;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.request.RequestRequestDto;
import com.example.sharesnotesapp.repository.RequestRepository;
import com.example.sharesnotesapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public void checkRequests(List<Request> requests) {
        if (!requests.isEmpty()) {
            for (Request request : requests) {
                if (request.getStatus().equals(Status.PENDING)) {
                    throw new IllegalArgumentException("There is already a request created");
                } else if (request.getStatus().equals(Status.ACCEPTED)) {
                    throw new IllegalArgumentException("Cannot send another request");
                }
            }
        }
    }

    @Override
    public Request sendRequest(RequestRequestDto requestDto) {
        User sender = userRepository.findById(requestDto.getSenderId())
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));
        User receiver = userRepository.findUserByEmail(requestDto.getReceiverEmail())
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Cannot send a request to yourself");
        }

        List<Request> existingRequests = requestRepository.getRequestsBySenderAndReceiver(sender, receiver);
        List<Request> requestsFromReceiver = requestRepository.getRequestsBySenderAndReceiver(receiver, sender);

        checkRequests(existingRequests);
        checkRequests(requestsFromReceiver);

        Request request = Request.builder()
                .sender(sender)
                .receiver(receiver)
                .sentAt(LocalDateTime.now())
                .build();

        return requestRepository.save(request);

    }

    @Override
    public void deleteRequest(Long id) {
        Request request = requestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Request does not exist"));
        if (!request.getStatus().equals(Status.PENDING)) {
            throw new IllegalArgumentException("Cannot delete a non-pending request");
        }

        requestRepository.deleteById(id);
    }
    @Override
    public void acceptRequest(Long id) {
        Request request = requestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Request does not exist"));
        if (!request.getStatus().equals(Status.PENDING)) {
            throw new IllegalArgumentException("Cannot accept a non-pending request");
        }

        request.setStatus(Status.ACCEPTED);
        requestRepository.save(request);
        addToFriendList(request.getSender(), request.getReceiver());
    }

    @Override
    public void declineRequest(Long id) {
        Request request = requestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Request does not exist"));
        if (!request.getStatus().equals(Status.PENDING)) {
            throw new IllegalArgumentException("Cannot decline a non-pending request");
        }

        request.setStatus(Status.DECLINED);
        requestRepository.save(request);
    }

    @Override
    public List<Request> getSentRequests(User user) {
        return requestRepository.getRequestsBySenderAndStatusOrderBySentAtDesc(user, Status.PENDING);
    }

    @Override
    public List<Request> getReceivedRequests(User user) {
        return requestRepository.getRequestsByReceiverAndStatusOrderBySentAtDesc(user, Status.PENDING);
    }

    @Override
    public Request getRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Request with id %s does not exist", id)));
    }

    @Override
    public void addToFriendList(User user, User friend) {
        if (user.getFriendList().contains(friend) || friend.getFriendList().contains(user)){
            throw new IllegalArgumentException("Users are already friends");
        }

        if (user.getId().equals(friend.getId())){
            throw new IllegalArgumentException("Cannot add yourself to friend list");
        }

        user.getFriendList().add(friend);
        friend.getFriendList().add(user);
        userRepository.save(user);
        userRepository.save(friend);
    }
}
