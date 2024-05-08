package com.example.sharesnotesapp.model.dto.mapper;

import com.example.sharesnotesapp.model.Note;
import com.example.sharesnotesapp.model.dto.response.NoteResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    NoteResponseDto toDto(Note note);
}
