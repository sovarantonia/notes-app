package com.example.sharesnotesapp.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class NoteRequestDto {
    private Long userId;
    @NotBlank
    private String title;
    private String text;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date date;
    @Min(value = 0, message = "Values must be between 0 and 10")
    @Max(value = 10, message = "Values must be between 0 and 10")
    private String grade = "0";
}
