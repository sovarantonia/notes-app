package com.example.sharesnotesapp.controller_test;

import com.example.sharesnotesapp.controller.NoteController;
import com.example.sharesnotesapp.model.FileType;
import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.mapper.NoteMapper;
import com.example.sharesnotesapp.model.dto.request.NoteRequestDto;
import com.example.sharesnotesapp.model.dto.response.NoteResponseDto;
import com.example.sharesnotesapp.model.dto.response.UserResponseDto;
import com.example.sharesnotesapp.service.note.NoteServiceImpl;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        note.setId(1L);
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

        UserResponseDto userResponseDto = new UserResponseDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
        NoteResponseDto noteResponseDto = new NoteResponseDto(userResponseDto, note.getId(), note.getTitle(), note.getText(), note.getDate(), note.getGrade());

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
    void testSaveNote_InvalidNoteDetails() throws Exception {
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
    void testGetNoteById() throws Exception {
        Long id = 1L;
        UserResponseDto userResponseDto = new UserResponseDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
        NoteResponseDto noteResponseDto = new NoteResponseDto(userResponseDto, note.getId(), note.getTitle(), note.getText(), note.getDate(), note.getGrade());

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
    void testGetNoteById_InvalidId() throws Exception {
        Long nonExistentId = 999L;

        when(noteService.getNoteById(nonExistentId))
                .thenThrow(new EntityNotFoundException(String.format("Note with id %s does not exist", nonExistentId)));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(get("/notes/{id}", nonExistentId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format("Note with id %s does not exist", nonExistentId)));
    }

    @Test
    void testGetNoteById_UserNotLoggedIn() throws Exception {
        Long id = 1L;

        when(noteService.getNoteById(id)).thenReturn(Optional.of(note));

        mockMvc.perform(get("/notes/{id}", id))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteNote() throws Exception {
        Long id = 1L;

        when(noteService.getNoteById(id)).thenReturn(Optional.of(note));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(delete("/notes/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteNote_InvalidId() throws Exception {
        Long nonExistentId = 999L;

        doThrow(new EntityNotFoundException("Note does not exist")).when(noteService).deleteNote(nonExistentId);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(delete("/notes/{id}", nonExistentId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Note does not exist"));
    }

    @Test
    void testDeleteNote_UserNotLoggedIn() throws Exception {
        Long id = 1L;

        when(noteService.getNoteById(id)).thenReturn(Optional.of(note));

        mockMvc.perform(delete("/notes/{id}", id))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateNote() throws Exception {
        Long id = 1L;
        UserResponseDto userResponseDto = new UserResponseDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
        NoteResponseDto noteResponseDto
                = new NoteResponseDto(userResponseDto, 1L , "New title", "Add new text", note.getDate(), 7);

        String requestBody = "{ \"title\": \"New title\", \"text\": \"Add new text\", \"date\": \"05-05-2024\", \"grade\": 7}";

        when(noteService.getNoteById(id)).thenReturn(Optional.of(note));
        when(mapper.toDto(noteService.updateNote(any(Long.class), any(NoteRequestDto.class)))).thenReturn(noteResponseDto);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(patch("/notes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.firstName", is("First-name")))
                .andExpect(jsonPath("$.user.lastName", is("Last-name")))
                .andExpect(jsonPath("$.user.email", is("email@test.com")))
                .andExpect(jsonPath("$.title", is("New title")))
                .andExpect(jsonPath("$.text", is("Add new text")))
                .andExpect(jsonPath("$.date", is("05-05-2024")))
                .andExpect(jsonPath("$.grade", is(7)));
    }

    @Test
    void testUpdateNote_InvalidDetails() throws Exception {
        Long id = 1L;
        String requestBody = "{ \"title\": \"\", \"text\": \"Add new text\", \"date\": \"05-05-2024\", \"grade\": 7}";

        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(patch("/notes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Title should not be empty")));
    }

    @Test
    void testUpdateNote_UserNotLoggedIn() throws Exception {
        Long id = 1L;
        String requestBody = "{ \"title\": \"title\", \"text\": \"Add new text\", \"date\": \"05-05-2024\", \"grade\": 7}";

        mockMvc.perform(patch("/notes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllNotesByUser() throws Exception {
        Note secondNote = new Note();
        secondNote.setUser(user);
        secondNote.setId(2L);
        secondNote.setTitle("Another title");
        secondNote.setDate(LocalDate.parse("2024-06-19"));
        secondNote.setText("text");
        secondNote.setGrade(7);

        UserResponseDto userResponseDto = new UserResponseDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
        NoteResponseDto noteResponseDto
                = new NoteResponseDto(userResponseDto, 1L, note.getTitle(), note.getText(), note.getDate(), note.getGrade());
        NoteResponseDto secondNoteResponseDto
                = new NoteResponseDto
                (userResponseDto, 2L, secondNote.getTitle(), secondNote.getText(), secondNote.getDate(), secondNote.getGrade());

        List<Note> notes = List.of(secondNote, note);

        when(noteService.getNotesByUser(any(User.class))).thenReturn(notes);
        when(mapper.toDto(secondNote)).thenReturn(secondNoteResponseDto);
        when(mapper.toDto(note)).thenReturn(noteResponseDto);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(get("/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Another title")))
                .andExpect(jsonPath("$[1].title", is("A title")))
                .andExpect(jsonPath("$[0].text", is("text")))
                .andExpect(jsonPath("$[1].text", is("Some text")))
                .andExpect(jsonPath("$[0].date", is("19-06-2024")))
                .andExpect(jsonPath("$[1].date", is("05-05-2024")))
                .andExpect(jsonPath("$[0].grade", is(7)))
                .andExpect(jsonPath("$[1].grade", is(9)))
                .andExpect(jsonPath("$[0].user.firstName", is("First-name")))
                .andExpect(jsonPath("$[1].user.firstName", is("First-name")));

    }

    @Test
    void testGetAllNotesByUser_UserNotLoggedIn() throws Exception {
        mockMvc.perform(get("/notes"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetNotesFilteredByTitle() throws Exception {
        Note secondNote = new Note();
        secondNote.setUser(user);
        secondNote.setId(2L);
        secondNote.setTitle("Fantastic day");
        secondNote.setDate(LocalDate.parse("2024-06-19"));
        secondNote.setText("text");
        secondNote.setGrade(7);

        String titleString = "day";

        UserResponseDto userResponseDto = new UserResponseDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
        NoteResponseDto secondNoteResponseDto
                = new NoteResponseDto
                (userResponseDto, secondNote.getId(), secondNote.getTitle(), secondNote.getText(), secondNote.getDate(), secondNote.getGrade());

        List<Note> notes = List.of(secondNote);

        when(noteService.getFilteredNotesByTitle(any(User.class), any(String.class))).thenReturn(notes);
        when(mapper.toDto(secondNote)).thenReturn(secondNoteResponseDto);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(get("/notes/filter")
                        .param("string", titleString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Fantastic day")))
                .andExpect(jsonPath("$[0].text", is("text")))
                .andExpect(jsonPath("$[0].date", is("19-06-2024")))
                .andExpect(jsonPath("$[0].grade", is(7)));
    }

    @Test
    void testGetNotesFilteredByTitle_UserNotLoggedIn() throws Exception {
        mockMvc.perform(get("/notes/filter"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDownloadNote_txt() throws Exception {
        Long id = 1L;

        String filename = "note_" + note.getTitle() + "_" + note.getDate() + ".";

        String fileContentStr = "Title: " + note.getTitle() + " " + note.getDate() + "\n\n" +
                "Content: " + "\n" + note.getText() + "\n\n" +
                "Grade: " + note.getGrade();

        byte[] fileContent = fileContentStr.getBytes();
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentLength(fileContent.length);
        headers.setContentDisposition(ContentDisposition
                .attachment()
                .filename(filename.concat(FileType.txt.toString()))
                .build());

        when(noteService.getNoteById(id)).thenReturn(Optional.of(note));
        when(noteService.createTextFileContent(any(Note.class))).thenReturn(fileContentStr);
        when(noteService.downloadNote(any(Note.class), any(FileType.class))).thenReturn(headers);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(get("/notes/{id}/download", id)
                        .param("type", FileType.txt.toString()))
                .andExpect(status().isOk())
                .andExpect(content().bytes(fileContent));
    }

    @Test
    void testDownloadNote_pdf() throws Exception {
        Long id = 1L;

        String filename = "note_" + note.getTitle() + "_" + note.getDate() + ".";

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        document.add(new Paragraph("Title: " + note.getTitle()));
        document.add(new Paragraph("Date: " + note.getDate()));
        document.add(new Paragraph("Content:"));
        document.add(new Paragraph(note.getText()));
        document.add(new Paragraph("Grade: " + note.getGrade()));
        document.close();

        byte[] fileContent = byteArrayOutputStream.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(fileContent.length);
        headers.setContentDisposition(ContentDisposition
                .attachment()
                .filename(filename.concat(FileType.pdf.toString()))
                .build());

        when(noteService.getNoteById(id)).thenReturn(Optional.of(note));
        when(noteService.createPdfContent(any(Note.class))).thenReturn(fileContent);
        when(noteService.downloadNote(any(Note.class), any(FileType.class))).thenReturn(headers);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(get("/notes/{id}/download", id)
                        .param("type", FileType.pdf.toString()))
                .andExpect(status().isOk())
                .andExpect(content().bytes(fileContent));


    }

    @Test
    void testDownloadNote_docx() throws Exception {
        Long id = 1L;

        String filename = "note_" + note.getTitle() + "_" + note.getDate() + ".";

        byte[] fileContent;

        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph titleParagraph = document.createParagraph();
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setBold(true);
            titleRun.setText("Title: " + note.getTitle());
            titleRun.addBreak();
            XWPFParagraph dateParagraph = document.createParagraph();
            XWPFRun dateRun = dateParagraph.createRun();
            dateRun.setText("Date: " + note.getDate());
            dateRun.addBreak();
            XWPFParagraph contentParagraph = document.createParagraph();
            XWPFRun contentRun = contentParagraph.createRun();
            contentRun.setText("Content:");
            contentRun.addBreak();
            contentRun.setText(note.getText());
            contentRun.addBreak();
            XWPFParagraph gradeParagraph = document.createParagraph();
            XWPFRun gradeRun = gradeParagraph.createRun();
            gradeRun.setText("Grade: " + note.getGrade());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.write(byteArrayOutputStream);

            fileContent =  byteArrayOutputStream.toByteArray();

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(fileContent.length);
            headers.setContentDisposition(ContentDisposition
                    .attachment()
                    .filename(filename.concat(FileType.docx.toString()))
                    .build());

            when(noteService.getNoteById(id)).thenReturn(Optional.of(note));
            when(noteService.createPdfContent(any(Note.class))).thenReturn(fileContent);
            when(noteService.downloadNote(any(Note.class), any(FileType.class))).thenReturn(headers);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            mockMvc.perform(get("/notes/{id}/download", id)
                            .param("type", FileType.docx.toString()))
                    .andExpect(status().isOk());

        } catch (IOException e) {
            throw new RuntimeException("Error while creating DOCX content", e);
        }
    }

    @Test
    void testDownloadNote_InvalidId() throws Exception {
        Long id = 999L;

        when(noteService.getNoteById(id)).thenThrow(new EntityNotFoundException(String.format("Note with id %s does not exist", id)));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(get("/notes/{id}/download", id)
                        .param("type", FileType.txt.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format("Note with id %s does not exist", id)));
    }

    @Test
    void testDownloadNote_InvalidFileType() throws Exception {
        Long id = 1L;

        when(noteService.getNoteById(id)).thenReturn(Optional.of(note));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(get("/notes/{id}/download", id)
                        .param("type", "another-type"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDownloadNote_UserNotLoggedIn() throws Exception {
        Long id = 1L;

        mockMvc.perform(get("/notes/{id}/download", id))
                .andExpect(status().isBadRequest());
    }
}
