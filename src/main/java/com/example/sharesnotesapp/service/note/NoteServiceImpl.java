package com.example.sharesnotesapp.service.note;

import com.example.sharesnotesapp.model.FileType;
import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.request.NoteRequestDto;
import com.example.sharesnotesapp.model.dto.response.NoteResponseDto;
import com.example.sharesnotesapp.repository.NoteRepository;
import com.example.sharesnotesapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;


import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    @Override
    public Note saveNote(Long id, NoteRequestDto noteRequestDto) {
        User associatedUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User does not exist"));
        if (!noteRequestDto.getGrade().matches("^(10|[0-9])$")) {
            throw new IllegalArgumentException("Invalid grade, must be an integer between 0 and 10");
        }
        Note createdNote = Note.builder()
                .user(associatedUser)
                .title(noteRequestDto.getTitle())
                .text(noteRequestDto.getText())
                .date(noteRequestDto.getDate())
                .grade(Integer.parseInt(noteRequestDto.getGrade()))
                .build();

        return noteRepository.save(createdNote);
    }

    @Override
    public void deleteNote(Long id) {
        if (noteRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Note does not exist");
        }

        noteRepository.deleteById(id);
    }

    @Override
    public Note updateNote(Long id, NoteRequestDto noteRequestDto) {

        Note updatedNote = noteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Note does not exist"));

        if (!(noteRequestDto.getTitle().isBlank() || noteRequestDto.getTitle().isEmpty())) {
            updatedNote.setTitle(noteRequestDto.getTitle());
        }

        if (!(noteRequestDto.getText().isBlank() || noteRequestDto.getText().isEmpty())) {
            updatedNote.setText(noteRequestDto.getText());
        }

        if (!(noteRequestDto.getGrade().isBlank() || noteRequestDto.getGrade().isEmpty())) {
            if (noteRequestDto.getGrade().matches("^(10|[0-9])$")) {
                updatedNote.setGrade(Integer.parseInt(noteRequestDto.getGrade()));
            } else {
                throw new IllegalArgumentException("Invalid grade, must be an integer between 0 and 10");
            }
        }

        return noteRepository.save(updatedNote);
    }

    @Override
    public Optional<Note> getNoteById(Long id) {
        return noteRepository.findById(id);
    }

    @Override
    public List<Note> getNotesByUser(User user) {
        return noteRepository.getNotesByUserOrderByDateDesc(user);
    }

    @Override
    public List<Note> getFilteredNotesByTitle(User user, String string) {
        if (!string.isEmpty() || !string.isBlank()) {
            return noteRepository.findAllByUserAndTitleContainsIgnoreCaseOrderByDateDesc(user, string);
        }

        return getNotesByUser(user);
    }

    @Override
    public HttpHeaders downloadNote(Note note, FileType type) {
        HttpHeaders headers = new HttpHeaders();
        String filename = "note_" + note.getTitle() + "_" + formatDate(note.getDate()) + ".txt";

        if (type.equals(FileType.text)) {
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
            headers.setContentLength(createFileContent(note).getBytes().length);

            return headers;

        }

        return null;
    }

    @Override
    public String createFileContent(Note note) {
        return "Title: " + note.getTitle() + " " + formatDate(note.getDate()) + "\n\n" +
                "Content: " + "\n" + note.getText() + "\n\n" +
                "Grade: " + note.getGrade();
    }

    @Override
    public String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        return format.format(date);
    }
}
