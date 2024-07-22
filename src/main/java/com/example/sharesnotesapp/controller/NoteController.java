package com.example.sharesnotesapp.controller;

import com.example.sharesnotesapp.model.FileType;
import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.mapper.NoteMapper;
import com.example.sharesnotesapp.model.dto.request.NoteRequestDto;
import com.example.sharesnotesapp.model.dto.response.NoteResponseDto;
import com.example.sharesnotesapp.service.note.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/notes")
public class NoteController {
    private final NoteService noteService;
    private final NoteMapper mapper;

    @Autowired
    public NoteController(NoteService noteService, NoteMapper mapper) {
        this.noteService = noteService;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseDto> getNoteById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            Note note = noteService.getNoteById(id).orElseThrow((() -> new EntityNotFoundException("No note with such id")));

            return ResponseEntity.ok(mapper.toDto(note));
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping
    public ResponseEntity<NoteResponseDto> createNote(@RequestBody @Valid NoteRequestDto noteRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user) {
            URI uri = URI.create((ServletUriComponentsBuilder.fromCurrentContextPath().path("/notes").toUriString()));

            return ResponseEntity.created(uri).body(mapper.toDto(noteService.saveNote(user.getId(), noteRequestDto)));
        }

        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNote(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
                noteService.deleteNote(id);

                return ResponseEntity.ok().build();
            }

        return ResponseEntity.badRequest().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<NoteResponseDto> updateNote(@PathVariable Long id, @RequestBody @Valid NoteRequestDto noteRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            Note updatedNote = noteService.updateNote(id, noteRequestDto);

            return ResponseEntity.ok(mapper.toDto(updatedNote));
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<List<NoteResponseDto>> getAllNotesByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user) {
            List<Note> notes = noteService.getNotesByUser(user);

            return ResponseEntity.ok(notes.stream().map(mapper::toDto).toList());
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<NoteResponseDto>> getNotesFilteredByTitle(@RequestParam(defaultValue = "") String string) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user) {
            List<Note> filteredNotes = noteService.getFilteredNotesByTitle(user, string);

            return ResponseEntity.ok(filteredNotes.stream().map(mapper::toDto).toList());
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadNote(@PathVariable Long id, @RequestParam String type) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            Note note = noteService.getNoteById(id).orElseThrow(EntityNotFoundException::new);
            byte[] fileContent = new byte[0];

            if (FileType.valueOf(type).equals(FileType.txt)) {
                fileContent = noteService.createTextFileContent(note).getBytes();
            } else if (FileType.valueOf(type).equals(FileType.pdf)) {
                fileContent = noteService.createPdfContent(note);
            } else if (FileType.valueOf(type).equals(FileType.docx)) {
                fileContent = noteService.createDocxContent(note);
            }
            else {
                ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok()
                    .headers(noteService.downloadNote(note, FileType.valueOf(type)))
                    .body(fileContent);

        }

        return ResponseEntity.badRequest().build();
    }
}
