package com.example.sharesnotesapp.model.dto.response;

import com.example.sharesnotesapp.model.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RequestResponseDto {
    private Long id;
    private UserResponseDto sender;
    private UserResponseDto receiver;
    private Status status;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime sentAt;

    public RequestResponseDto(UserResponseDto sender, UserResponseDto receiver, Status status, LocalDateTime sentAt){
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
        this.sentAt = sentAt;
    }
}
