package com.example.sharesnotesapp.service.request;

import com.example.sharesnotesapp.model.Request;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.request.RequestRequestDto;

import java.util.List;

public interface RequestService {
    void checkRequests(List<Request> requests);
    Request sendRequest(RequestRequestDto requestDto);
    void deleteRequest(Long id);
    void acceptRequest(Long id);
    void declineRequest(Long id);
    List<Request> getSentRequests(User user);
    List<Request> getReceivedRequests(User user);
    Request getRequestById(Long id);
}
