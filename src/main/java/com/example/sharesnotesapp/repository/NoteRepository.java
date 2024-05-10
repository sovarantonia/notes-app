package com.example.sharesnotesapp.repository;

import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    ArrayList<Note> getNotesByUserOrderByDateDesc(User user);
}
