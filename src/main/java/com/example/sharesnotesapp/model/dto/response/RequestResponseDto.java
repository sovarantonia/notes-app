package com.example.sharesnotesapp.model.dto.response;

import com.example.sharesnotesapp.model.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class RequestResponseDto {
    private UserResponseDto senderInfo;
    private UserResponseDto receiverInfo;
    private Status status;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDate sentAt;
}
