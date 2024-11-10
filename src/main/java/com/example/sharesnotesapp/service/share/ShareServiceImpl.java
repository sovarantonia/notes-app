package com.example.sharesnotesapp.service.share;

import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.Share;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.repository.NoteRepository;
import com.example.sharesnotesapp.repository.ShareRepository;
import com.example.sharesnotesapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {
    private final ShareRepository shareRepository;
    private final UserRepository userRepository;
    private final NoteRepository noteRepository;

    @Override
    public Share shareNote(User sender, String receiverEmail, Long noteId) {
        User receiver = userRepository.findUserByEmail(receiverEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User does not exist"));

        Note sharedNote = noteRepository.findById(noteId)
                .orElseThrow(() -> new EntityNotFoundException("Note does not exist"));

        Share share = new Share();
        share.setSender(sender);
        share.setReceiver(receiver);
        share.setSentNote(sharedNote);
        share.setSentAt(LocalDate.now());

        return shareRepository.save(share);
    }

    @Override
    public List<Share> getAllSharedNotesBetweenUsers(User user, String receiverEmail) {
        if (receiverEmail.isEmpty()) {
            return shareRepository.getSharesBySenderOrderBySentAtDesc(user);
        } else {
            User receiver = userRepository.findUserByEmail(receiverEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User does not exist"));

            return shareRepository.getSharesBySenderAndReceiverOrderBySentAtDesc(user, receiver);
        }
    }

    @Override
    public List<Share> getAllReceivedNotesBetweenUsers(User user, String senderEmail) {
        if (senderEmail.isEmpty()) {
            return shareRepository.getSharesByReceiverOrderBySentAtDesc(user);
        } else {
            User sender = userRepository.findUserByEmail(senderEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User does not exist"));

            return shareRepository.getSharesBySenderAndReceiverOrderBySentAtDesc(sender, user);
        }
    }

    @Override
    public Share getShareById(Long id) {
        return shareRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Share with id %s does not exist", id)));
    }
}
