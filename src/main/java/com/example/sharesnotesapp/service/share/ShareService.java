package com.example.sharesnotesapp.service.share;

import com.example.sharesnotesapp.model.Share;
import com.example.sharesnotesapp.model.User;

import java.util.List;

public interface ShareService {
    Share shareNote(User sender, String receiverEmail, Long noteId);
    List<Share> getAllSharedNotesBetweenUsers(User user, String receiverEmail);
    List<Share> getAllReceivedNotesBetweenUsers(User user, String senderEmail);
    Share getShareById(Long id);
}
