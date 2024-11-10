package com.example.sharesnotesapp.repository;

import com.example.sharesnotesapp.model.Share;
import com.example.sharesnotesapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShareRepository extends JpaRepository<Share, Long> {
    List<Share> getSharesBySenderAndReceiverOrderBySentAtDesc(User sender, User receiver);
    List<Share> getSharesBySenderOrderBySentAtDesc(User sender);
    List<Share> getSharesByReceiverOrderBySentAtDesc(User receiver);
}
