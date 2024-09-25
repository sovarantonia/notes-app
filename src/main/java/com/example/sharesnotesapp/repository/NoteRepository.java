package com.example.sharesnotesapp.repository;

import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> getNotesByUserOrderByDateDesc(User user);
    List<Note> findAllByUserAndTitleContainsIgnoreCaseOrderByDateDesc(User user, String string);
    List<Note> getFirst5ByUserOrderByDateDesc(User user);
}
