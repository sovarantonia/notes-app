package com.example.sharesnotesapp.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ShareResponseDto {
    private Long id;
    private UserInfoDto sender;
    private UserInfoDto receiver;
    private NoteResponseDto sentNote;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate sentAt;

    public ShareResponseDto(UserInfoDto sender, UserInfoDto receiver, NoteResponseDto sharedNote, LocalDate sentAt){
        this.sender = sender;
        this.receiver = receiver;
        this.sentNote = sharedNote;
        this.sentAt = sentAt;
    }
}
