package com.example.sharesnotesapp.service.request;

public interface RequestService {
    void sendRequest(Long senderId, Long receiverId);
    void deleteRequest(Long id);
    void acceptRequest(Long id);
    void declineRequest(Long id);
}
