package com.example.sharesnotesapp.model.dto.response;

import com.example.sharesnotesapp.model.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RequestResponseDto {
    private UserResponseDto sender;
    private UserResponseDto receiver;
    private Status status;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime sentAt;
}
