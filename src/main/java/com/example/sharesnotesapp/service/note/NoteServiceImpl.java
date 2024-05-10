package com.example.sharesnotesapp.service.note;

import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.request.NoteRequestDto;
import com.example.sharesnotesapp.repository.NoteRepository;
import com.example.sharesnotesapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
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
    public ArrayList<Note> getNotesByUser(User user) {
        return noteRepository.getNotesByUserOrderByDateDesc(user);
    }
}
