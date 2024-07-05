package com.example.sharesnotesapp.service.note;

import com.example.sharesnotesapp.model.FileType;
import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.request.NoteRequestDto;
import com.example.sharesnotesapp.repository.NoteRepository;
import com.example.sharesnotesapp.repository.UserRepository;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
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
import java.text.SimpleDateFormat;
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
        String filename = "note_" + note.getTitle() + "_" + formatDate(note.getDate()) + ".";

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
        return "Title: " + note.getTitle() + " " + formatDate(note.getDate()) + "\n\n" +
                "Content: " + "\n" + note.getText() + "\n\n" +
                "Grade: " + note.getGrade();
    }

    @Override
    public byte[] createPdfContent(Note note) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("Title: " + note.getTitle()));
        document.add(new Paragraph("Date: " + formatDate(note.getDate())));
        document.add(new Paragraph("Content:"));
        document.add(new Paragraph(note.getText()));
        document.add(new Paragraph("Grade: " + note.getGrade()));

        document.close();

        return byteArrayOutputStream.toByteArray();
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
            dateRun.setText("Date: " + formatDate(note.getDate()));
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

    @Override
    public String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        return format.format(date);
    }
}
