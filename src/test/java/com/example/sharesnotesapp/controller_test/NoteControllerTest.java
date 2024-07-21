package com.example.sharesnotesapp.controller_test;

import com.example.sharesnotesapp.controller.NoteController;
import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.mapper.NoteMapper;
import com.example.sharesnotesapp.model.dto.request.NoteRequestDto;
import com.example.sharesnotesapp.model.dto.response.NoteResponseDto;
import com.example.sharesnotesapp.model.dto.response.UserResponseDto;
import com.example.sharesnotesapp.service.note.NoteServiceImpl;
import com.example.sharesnotesapp.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@WebMvcTest(NoteController.class)
@Import(TestSecurityConfig.class)
class NoteControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoteServiceImpl noteService;

    @MockBean
    private NoteMapper mapper;

    private Authentication authentication;
    private User user;
    private Note note;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setFirstName("First-name");
        user.setLastName("Last-name");
        user.setPassword("test123");
        user.setEmail("email@test.com");

        note = new Note();
        note.setUser(user);
        note.setTitle("A title");
        note.setText("Some text");
        note.setGrade(9);
        note.setDate(LocalDate.parse("2024-05-05"));

        authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), Collections.emptyList());
    }

    @Test
    void testSaveNote() throws Exception {
        NoteRequestDto noteRequestDto = new NoteRequestDto();
        noteRequestDto.setTitle("A title");
        noteRequestDto.setText("Some text");
        noteRequestDto.setDate(LocalDate.parse("2024-05-05"));
        noteRequestDto.setGrade(9);

        UserResponseDto userResponseDto = new UserResponseDto(user.getFirstName(), user.getLastName(), user.getEmail());
        NoteResponseDto noteResponseDto = new NoteResponseDto(userResponseDto, note.getTitle(), note.getText(), note.getDate(), note.getGrade());

        String requestBody = "{ \"title\": \"A title\", \"text\": \"Some text\", \"date\": \"05-05-2024\", \"grade\": 9}";

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(noteService.saveNote(any(Long.class), any(NoteRequestDto.class))).thenReturn(note);
        when(mapper.toDto(note)).thenReturn(noteResponseDto);

        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.firstName", is("First-name")))
                .andExpect(jsonPath("$.user.lastName", is("Last-name")))
                .andExpect(jsonPath("$.user.email", is("email@test.com")))
                .andExpect(jsonPath("$.title", is("A title")))
                .andExpect(jsonPath("$.text", is("Some text")))
                .andExpect(jsonPath("$.date", is("05-05-2024")))
                .andExpect(jsonPath("$.grade", is(9)));
    }

    @Test
    void testSaveNote_InvalidNoteDetails() throws Exception{
        String requestBody = "{ \"title\": \"\", \"text\": \"Some text\", \"date\": \"05-05-2024\", \"grade\": 9}";


        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Title should not be empty")));
    }

    @Test
    void testSaveNote_UserNotLoggedIn() throws Exception {
        String requestBody = "{ \"title\": \"A title\", \"text\": \"Some text\", \"date\": \"05-05-2024\", \"grade\": 9}";

        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetNoteById() throws Exception{
        Long id = 1L;
        UserResponseDto userResponseDto = new UserResponseDto(user.getFirstName(), user.getLastName(), user.getEmail());
        NoteResponseDto noteResponseDto = new NoteResponseDto(userResponseDto, note.getTitle(), note.getText(), note.getDate(), note.getGrade());

        when(noteService.getNoteById(id)).thenReturn(Optional.of(note));
        when(mapper.toDto(note)).thenReturn(noteResponseDto);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(get("/notes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.firstName", is("First-name")))
                .andExpect(jsonPath("$.user.lastName", is("Last-name")))
                .andExpect(jsonPath("$.user.email", is("email@test.com")))
                .andExpect(jsonPath("$.title", is("A title")))
                .andExpect(jsonPath("$.text", is("Some text")))
                .andExpect(jsonPath("$.date", is("05-05-2024")))
                .andExpect(jsonPath("$.grade", is(9)));
    }

    @Test
    void testGetNoteById_InvalidId() throws Exception{
        Long nonExistentId = 999L;

        when(noteService.getNoteById(nonExistentId))
                .thenThrow(new EntityNotFoundException(String.format("Note with id %s does not exist", nonExistentId)));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(get("/notes/{id}", nonExistentId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format("Note with id %s does not exist", nonExistentId)));
    }

    @Test
    void testGetNoteById_UserNotLoggedIn() throws Exception{
        Long id = 1L;

        when(noteService.getNoteById(id)).thenReturn(Optional.of(note));

        mockMvc.perform(get("/notes/{id}", id))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteNote() throws Exception{
        Long id = 1L;

        when(noteService.getNoteById(id)).thenReturn(Optional.of(note));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(delete("/notes/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteNote_InvalidId() throws Exception{
        Long nonExistentId = 999L;

        doThrow(new EntityNotFoundException("Note does not exist")).when(noteService).deleteNote(nonExistentId);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(delete("/notes/{id}", nonExistentId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Note does not exist"));
    }

    @Test
    void testDeleteNote_UserNotLoggedIn() throws Exception{
        Long id = 1L;

        when(noteService.getNoteById(id)).thenReturn(Optional.of(note));

        mockMvc.perform(delete("/notes/{id}", id))
                .andExpect(status().isBadRequest());
    }
}
