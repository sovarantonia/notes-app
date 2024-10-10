package com.example.sharesnotesapp.repository;

import com.example.sharesnotesapp.model.Request;
import com.example.sharesnotesapp.model.Status;
import com.example.sharesnotesapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> getRequestsBySenderAndReceiver(User sender, User receiver);
    List<Request> getRequestsBySenderAndStatusOrderBySentAtDesc(User sender, Status status);
    List<Request> getRequestsByReceiverAndStatusOrderBySentAtDesc(User receiver, Status status);
}
