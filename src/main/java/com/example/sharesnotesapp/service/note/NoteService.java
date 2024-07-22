package com.example.sharesnotesapp.service.note;

import com.example.sharesnotesapp.model.FileType;
import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.request.NoteRequestDto;
import org.springframework.http.HttpHeaders;


import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface NoteService {
    Note saveNote(Long id, NoteRequestDto noteRequestDto);

    void deleteNote(Long id);

    Note updateNote(Long id, NoteRequestDto noteRequestDto);
    Optional<Note> getNoteById(Long id);

    List<Note> getNotesByUser(User user);

    List<Note> getFilteredNotesByTitle(User user, String string);

    HttpHeaders downloadNote(Note note, FileType type);

    String createTextFileContent(Note note);

    byte[] createPdfContent(Note note);

    byte[] createDocxContent(Note note);
}
