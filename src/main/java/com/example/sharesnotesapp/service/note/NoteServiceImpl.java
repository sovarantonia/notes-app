package com.example.sharesnotesapp.service.note;

import com.example.sharesnotesapp.model.FileType;
import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.request.NoteRequestDto;
import com.example.sharesnotesapp.repository.NoteRepository;
import com.example.sharesnotesapp.repository.UserRepository;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    @Override
    public Note saveNote(Long userId, NoteRequestDto noteRequestDto) {
        User associatedUser = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        if (noteRequestDto.getGrade() == null) {
            throw new IllegalArgumentException("Grade must be an integer between 1 and 10");
        }

        Note createdNote = Note.builder()
                .user(associatedUser)
                .title(noteRequestDto.getTitle())
                .text(noteRequestDto.getText())
                .date(noteRequestDto.getDate())
                .grade(noteRequestDto.getGrade())
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

        if (!noteRequestDto.getTitle().isBlank() && !noteRequestDto.getTitle().isEmpty()) {
            updatedNote.setTitle(noteRequestDto.getTitle());
        }

        if (!noteRequestDto.getText().isBlank() && !noteRequestDto.getText().isEmpty()) {
            updatedNote.setText(noteRequestDto.getText());
        }

        if (noteRequestDto.getGrade() != 0) {
            updatedNote.setGrade(noteRequestDto.getGrade());
        }

        return noteRepository.save(updatedNote);
    }


    @Override
    public Optional<Note> getNoteById(Long id) {
        if (noteRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException(String.format("Note with id %s does not exist", id));
        }
        return noteRepository.findById(id);
    }

    @Override
    public List<Note> getNotesByUser(User user) {
        return noteRepository.getNotesByUserOrderByDateDesc(user);
    }

    @Override
    public List<Note> getFilteredNotesByTitle(User user, String string) {
        if (!string.isEmpty() && !string.isBlank()) {
            return noteRepository.findAllByUserAndTitleContainsIgnoreCaseOrderByDateDesc(user, string);
        }

        return noteRepository.getNotesByUserOrderByDateDesc(user);
    }

    @Override
    public List<Note> getLatestNotes(User user) {
        return noteRepository.getFirst5ByUserOrderByDateDesc(user);
    }

    @Override
    public List<Note> getNotesBetweenDates(LocalDate startDate, LocalDate endDate) {
        return noteRepository.getNotesByDateBetweenOrderByDateAsc(startDate, endDate);
    }

    @Override
    public HttpHeaders downloadNote(Note note, FileType type) {
        HttpHeaders headers = new HttpHeaders();
        String filename = "note_" + note.getTitle() + "_" + note.getDate() + ".";

        if (type.equals(FileType.txt)) {
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentLength(createTextFileContent(note).getBytes().length);
            headers.setContentDisposition(ContentDisposition
                    .attachment()
                    .filename(filename.concat(FileType.txt.toString()))
                    .build());

        } else if (type.equals(FileType.pdf)) {

            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentLength(createPdfContent(note).length);
            headers.setContentDisposition(ContentDisposition
                    .attachment()
                    .filename(filename.concat(FileType.pdf.toString()))
                    .build());

        } else if (type.equals(FileType.docx)) {
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(createDocxContent(note).length);
            headers.setContentDisposition(ContentDisposition
                    .attachment()
                    .filename(filename.concat(FileType.docx.toString()))
                    .build());
        }

        return headers;
    }


    @Override
    public String createTextFileContent(Note note) {
        return "Title: " + note.getTitle() + " " + note.getDate() + "\n\n" +
                "Content: " + "\n" + note.getText() + "\n\n" +
                "Grade: " + note.getGrade();
    }

    @Override
    public byte[] createPdfContent(Note note) {
        Document document = new Document();
        byte[] pdfBytes;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            document.add(new Paragraph("Title: " + note.getTitle()));
            document.add(new Paragraph("Date: " + note.getDate()));
            document.add(new Paragraph("Content:"));
            document.add(new Paragraph(note.getText()));
            document.add(new Paragraph("Grade: " + note.getGrade()));

            document.close();
            pdfBytes = byteArrayOutputStream.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Error creating PDF document", e);
        }

        return pdfBytes;
    }

    @Override
    public byte[] createDocxContent(Note note) {
        try (XWPFDocument document = new XWPFDocument()) {
            // Create a title paragraph
            XWPFParagraph titleParagraph = document.createParagraph();
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setBold(true);
            titleRun.setText("Title: " + note.getTitle());
            titleRun.addBreak();

            // Create a date paragraph
            XWPFParagraph dateParagraph = document.createParagraph();
            XWPFRun dateRun = dateParagraph.createRun();
            dateRun.setText("Date: " + note.getDate());
            dateRun.addBreak();

            // Create a content paragraph
            XWPFParagraph contentParagraph = document.createParagraph();
            XWPFRun contentRun = contentParagraph.createRun();
            contentRun.setText("Content:");
            contentRun.addBreak();
            contentRun.setText(note.getText());
            contentRun.addBreak();

            //Create grade paragraph
            XWPFParagraph gradeParagraph = document.createParagraph();
            XWPFRun gradeRun = gradeParagraph.createRun();
            gradeRun.setText("Grade: " + note.getGrade());

            // Write the document to a byte array output stream
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.write(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error while creating DOCX content", e);
        }
    }
}
