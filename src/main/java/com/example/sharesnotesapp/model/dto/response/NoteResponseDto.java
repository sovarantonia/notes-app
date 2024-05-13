package com.example.sharesnotesapp.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class NoteResponseDto {
    private UserResponseDto user;
    private String title;
    private String text;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date date;
    private String grade;
}
