package com.example.sharesnotesapp.service.note;

import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.request.NoteRequestDto;

import java.util.ArrayList;
import java.util.Optional;

public interface NoteService {
    Note saveNote(Long id, NoteRequestDto noteRequestDto);

    void deleteNote(Long id);

    Note updateNote(Long id, NoteRequestDto noteRequestDto);
    Optional<Note> getNoteById(Long id);

    ArrayList<Note> getNotesByUser(User user);
}
