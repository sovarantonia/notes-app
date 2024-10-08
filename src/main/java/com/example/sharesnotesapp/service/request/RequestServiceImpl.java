package com.example.sharesnotesapp.service.request;

import com.example.sharesnotesapp.model.Request;
import com.example.sharesnotesapp.model.Status;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.repository.RequestRepository;
import com.example.sharesnotesapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService{
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public void sendRequest(Long senderId, Long receiverId) {
        if(senderId.equals(receiverId)){
            throw new IllegalArgumentException("Cannot send a request to yourself");
        }
        User sender = userRepository.findById(senderId).orElseThrow(() -> new EntityNotFoundException("User does not exist"));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        List<Request> existingRequests = requestRepository.getRequestsBySenderAndReceiver(sender, receiver);

        if(!existingRequests.isEmpty()){
            for(Request request : existingRequests){
                if (request.getStatus().equals(Status.PENDING)){
                    throw new IllegalArgumentException("There is already a request created");
                }
                else if (request.getStatus().equals(Status.ACCEPTED)){
                    throw new IllegalArgumentException("Cannot send another request");
                }
            }
        }

        Request request = Request.builder()
                .sender(sender)
                .receiver(receiver)
                .sentAt(LocalDate.now())
                .build();

        requestRepository.save(request);

    }

    @Override
    public void deleteRequest(Long id) {
        Request request = requestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Request does not exist"));
        if(!request.getStatus().equals(Status.PENDING)){
            throw new IllegalArgumentException("Cannot delete a non-pending request");
        }

        requestRepository.deleteById(id);
    }

    @Override
    public void acceptRequest(Long id) {
        Request request = requestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Request does not exist"));
        if(!request.getStatus().equals(Status.PENDING)){
            throw new IllegalArgumentException("Cannot accept a non-pending request");
        }

        request.setStatus(Status.ACCEPTED);
        requestRepository.save(request);
    }

    @Override
    public void declineRequest(Long id) {
        Request request = requestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Request does not exist"));
        if(!request.getStatus().equals(Status.PENDING)){
            throw new IllegalArgumentException("Cannot decline a non-pending request");
        }

        request.setStatus(Status.DECLINED);
        requestRepository.save(request);
    }
}
