package com.example.sharesnotesapp.service_test;

import com.example.sharesnotesapp.model.FileType;
import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.request.NoteRequestDto;
import com.example.sharesnotesapp.repository.NoteRepository;
import com.example.sharesnotesapp.repository.UserRepository;
import com.example.sharesnotesapp.service.note.NoteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.persistence.EntityNotFoundException;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NoteServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteServiceImpl noteService;
    private User user;
    private Note note;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setFirstName("First-Name");
        user.setLastName("Last-name");
        user.setEmail("email@email.com");

        note = new Note();
        note.setId(1L);
        note.setTitle("A title");
        note.setText("Text");
        note.setGrade(8);
        note.setDate(Date.valueOf("2022-10-10"));
    }

    @Test
    void testSaveNote() {
        note.setUser(user);

        when(noteRepository.save(any(Note.class))).thenReturn(note);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        NoteRequestDto noteRequestDto = new NoteRequestDto();
        noteRequestDto.setUserId(user.getId());
        noteRequestDto.setTitle("A title");
        noteRequestDto.setText("Text");
        noteRequestDto.setDate(Date.valueOf("2022-10-10"));
        noteRequestDto.setGrade(8);

        Note savedNote = noteService.saveNote(user.getId(), noteRequestDto);

        assertEquals(note.getUser(), savedNote.getUser());
        assertEquals(note.getTitle(), savedNote.getTitle());
        assertEquals(note.getText(), savedNote.getText());
        assertEquals(note.getGrade(), savedNote.getGrade());
        assertEquals(note.getDate(), savedNote.getDate());

        verify(noteRepository).save(any(Note.class));
    }

    @Test
    void testSaveNote_InvalidUser() {
        Long nonExistentUserId = 100L;

        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        NoteRequestDto noteRequestDto = new NoteRequestDto();

        EntityNotFoundException exception = assertThrows
                (EntityNotFoundException.class, () -> noteService.saveNote(nonExistentUserId, noteRequestDto));

        String message = "User does not exist";

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testDeleteNote() {
        when(noteRepository.findById(note.getId())).thenReturn(Optional.of(note));

        noteService.deleteNote(note.getId());

        verify(noteRepository, times(1)).deleteById(note.getId());
    }

    @Test
    void testDeleteNote_InvalidId() {
        Long nonExistentId = 100L;

        when(noteRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> noteService.deleteNote(nonExistentId));

        String message = "Note does not exist";

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testGetNoteById() {
        Long id = 1L;

        when(noteRepository.findById(note.getId())).thenReturn(Optional.of(note));

        Note foundNote = noteService.getNoteById(id).orElseThrow();

        assertEquals(note.getUser(), foundNote.getUser());
        assertEquals(note.getTitle(), foundNote.getTitle());
        assertEquals(note.getText(), foundNote.getText());
        assertEquals(note.getGrade(), foundNote.getGrade());
        assertEquals(note.getDate(), foundNote.getDate());
    }

    @Test
    void testGetNoteById_InvalidId() {
        Long nonExistentId = 100L;

        when(noteRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> noteService.getNoteById(nonExistentId));

        String message = String.format("Note with id %s does not exist", nonExistentId);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testGetNotesByUser() {
        Note secondNote = new Note();
        secondNote.setId(2L);
        secondNote.setUser(user);
        secondNote.setTitle("Title2");
        secondNote.setText("Text2");
        secondNote.setDate(Date.valueOf("2024-12-12"));

        when(noteRepository.getNotesByUserOrderByDateDesc(user)).thenReturn(List.of(note, secondNote));

        List<Note> foundNotes = noteService.getNotesByUser(user);

        assertEquals(note, foundNotes.get(0));
        assertEquals(secondNote, foundNotes.get(1));
    }

    @Test
    void testGetNotesByUser_NoNotesForUser() {
        User newUser = new User();
        when(noteRepository.getNotesByUserOrderByDateDesc(newUser)).thenReturn(List.of());

        List<Note> foundNotes = noteService.getNotesByUser(newUser);

        assertEquals(0, foundNotes.size());
    }

    @Test
    void testGetFilteredNotesByTitle() {
        Note secondNote = new Note();
        secondNote.setId(2L);
        secondNote.setUser(user);
        secondNote.setTitle("Something");
        secondNote.setText("Text2");
        secondNote.setDate(Date.valueOf("2024-12-12"));

        String titleString = "Some";

        when(noteRepository.findAllByUserAndTitleContainsIgnoreCaseOrderByDateDesc(user, titleString)).thenReturn(List.of(secondNote));

        List<Note> foundNotes = noteService.getFilteredNotesByTitle(user, titleString);

        assertEquals(secondNote, foundNotes.get(0));
    }

    @Test
    void testGetFilteredNotesByTitle_SmallCase(){
        Note secondNote = new Note();
        secondNote.setId(2L);
        secondNote.setUser(user);
        secondNote.setTitle("Something");
        secondNote.setText("Text2");
        secondNote.setDate(Date.valueOf("2024-12-12"));

        String titleString = "some";

        when(noteRepository.findAllByUserAndTitleContainsIgnoreCaseOrderByDateDesc(user, titleString)).thenReturn(List.of(secondNote));

        List<Note> foundNotes = noteService.getFilteredNotesByTitle(user, titleString);

        assertEquals(secondNote, foundNotes.get(0));
    }

    @Test
    void testGetFilteredNotesByTitle_NoResults(){
        Note secondNote = new Note();
        secondNote.setId(2L);
        secondNote.setUser(user);
        secondNote.setTitle("Something");
        secondNote.setText("Text2");
        secondNote.setDate(Date.valueOf("2024-12-12"));

        String titleString = "random";

        when(noteRepository.findAllByUserAndTitleContainsIgnoreCaseOrderByDateDesc(user, titleString)).thenReturn(List.of());

        List<Note> foundNotes = noteService.getFilteredNotesByTitle(user, titleString);

        assertEquals(0, foundNotes.size());
    }

    @Test
    void testGetFilteredNotesByTitle_EmptyString(){
        Note secondNote = new Note();
        secondNote.setId(2L);
        secondNote.setUser(user);
        secondNote.setTitle("Something");
        secondNote.setText("Text2");
        secondNote.setDate(Date.valueOf("2024-12-12"));

        String titleString = "";

        when(noteRepository.getNotesByUserOrderByDateDesc(user)).thenReturn(List.of(secondNote, note));

        List<Note> foundNotes = noteService.getFilteredNotesByTitle(user, titleString);

        assertEquals(2, foundNotes.size());
        assertEquals(secondNote, foundNotes.get(0));
        assertEquals(note, foundNotes.get(1));
    }

    @Test
    void testGetFilteredNotesByTitle_BlankString(){
        Note secondNote = new Note();
        secondNote.setId(2L);
        secondNote.setUser(user);
        secondNote.setTitle("Something");
        secondNote.setText("Text2");
        secondNote.setDate(Date.valueOf("2024-12-12"));

        String titleString = " ";

        when(noteRepository.getNotesByUserOrderByDateDesc(user)).thenReturn(List.of(secondNote, note));

        List<Note> foundNotes = noteService.getFilteredNotesByTitle(user, titleString);

        assertEquals(2, foundNotes.size());
        assertEquals(secondNote, foundNotes.get(0));
        assertEquals(note, foundNotes.get(1));
    }

    @Test
    void testGerFilteredNotesByTitle_MoreResults(){
        Note secondNote = new Note();
        secondNote.setId(2L);
        secondNote.setUser(user);
        secondNote.setTitle("Title2");
        secondNote.setText("Text2");
        secondNote.setDate(Date.valueOf("2024-12-12"));

        String titleString = "title";

        when(noteRepository.findAllByUserAndTitleContainsIgnoreCaseOrderByDateDesc(user, titleString)).thenReturn(List.of(secondNote, note));

        List<Note> foundNotes = noteService.getFilteredNotesByTitle(user, titleString);

        assertEquals(2, foundNotes.size());
        assertEquals(secondNote, foundNotes.get(0));
        assertEquals(note, foundNotes.get(1));
    }

    @Test
    void testGetFilteredNotesByTitle_TitleWithMoreWords(){
        Note secondNote = new Note();
        secondNote.setId(2L);
        secondNote.setUser(user);
        secondNote.setTitle("Title2");
        secondNote.setText("Text2");
        secondNote.setDate(Date.valueOf("2024-12-12"));

        String titleString = "fantastic title";

        when(noteRepository.findAllByUserAndTitleContainsIgnoreCaseOrderByDateDesc(user, titleString)).thenReturn(List.of());

        List<Note> foundNotes = noteService.getFilteredNotesByTitle(user, titleString);

        assertEquals(0, foundNotes.size());
        verify(noteRepository, times(1)).findAllByUserAndTitleContainsIgnoreCaseOrderByDateDesc(user, titleString);
        verify(noteRepository, never()).getNotesByUserOrderByDateDesc(user);
    }

    @Test
    void testUpdateNote(){
        when(noteRepository.findById(note.getId())).thenReturn(Optional.of(note));

        NoteRequestDto noteRequestDto = new NoteRequestDto();
        noteRequestDto.setTitle("New title");
        noteService.updateNote(note.getId(), noteRequestDto);

        assertEquals("New title", note.getTitle());
        assertEquals("Text", note.getText());
        assertEquals(Date.valueOf("2022-10-10"), note.getDate());
        assertEquals(8, note.getGrade());
    }

    @Test
    void testUpdateNote_InvalidId(){
        Long nonExistentId = 332L;
        NoteRequestDto noteRequestDto = new NoteRequestDto();
        noteRequestDto.setTitle("Title title");

        when(noteRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows
                (EntityNotFoundException.class, () -> noteService.updateNote(nonExistentId, noteRequestDto));

        assertEquals("Note does not exist", exception.getMessage());
    }

    @Test
    void testDownloadNoteTxt() {
        HttpHeaders headers = noteService.downloadNote(note, FileType.txt);

        assertEquals(MediaType.TEXT_PLAIN, headers.getContentType());
        assertTrue(headers.getContentLength() > 0);
        assertTrue(headers.getContentDisposition().toString().contains("filename=\"note_A title"));
    }

    @Test
    void testDownloadNotePdf() {
        HttpHeaders headers = noteService.downloadNote(note, FileType.pdf);

        assertEquals(MediaType.APPLICATION_PDF, headers.getContentType());
        assertTrue(headers.getContentLength() > 0);
        assertTrue(headers.getContentDisposition().toString().contains("filename=\"note_A title"));
    }

    @Test
    void testDownloadNoteDocx() {
        HttpHeaders headers = noteService.downloadNote(note, FileType.docx);

        assertEquals(MediaType.APPLICATION_OCTET_STREAM, headers.getContentType());
        assertTrue(headers.getContentLength() > 0);
        assertTrue(headers.getContentDisposition().toString().contains("filename=\"note_A title"));
    }
}



