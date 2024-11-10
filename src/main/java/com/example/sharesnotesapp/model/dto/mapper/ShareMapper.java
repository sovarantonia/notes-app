package com.example.sharesnotesapp.model.dto.mapper;

import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.Share;
import com.example.sharesnotesapp.model.dto.response.NoteResponseDto;
import com.example.sharesnotesapp.model.dto.response.ShareResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShareMapper {
    ShareResponseDto toDto(Share share);
    NoteResponseDto toNoteDto(Note note);
}
