package com.example.sharesnotesapp.controller;

import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.mapper.NoteMapper;
import com.example.sharesnotesapp.model.dto.request.NoteRequestDto;
import com.example.sharesnotesapp.model.dto.response.NoteResponseDto;
import com.example.sharesnotesapp.service.note.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.net.URI;


@RestController
@RequiredArgsConstructor
@RequestMapping("/notes")
public class NoteController {
    @Autowired
    private final NoteService noteService;
    @Autowired
    private final NoteMapper mapper;

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseDto> getNoteById(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User){
            Note note = noteService.getNoteById(id).orElseThrow((()-> new EntityNotFoundException("No note with such id")));

            return ResponseEntity.ok(mapper.toDto(note));
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping
    public ResponseEntity<NoteResponseDto> createNote(@RequestBody NoteRequestDto noteRequestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user){
            URI uri = URI.create((ServletUriComponentsBuilder.fromCurrentContextPath().path("/notes").toUriString()));

            return ResponseEntity.created(uri).body(mapper.toDto(noteService.saveNote(user.getId(), noteRequestDto)));
        }

        return ResponseEntity.badRequest().build();
    }

}
