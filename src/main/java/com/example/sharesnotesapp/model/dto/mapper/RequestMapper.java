package com.example.sharesnotesapp.model.dto.mapper;

import com.example.sharesnotesapp.model.Request;
import com.example.sharesnotesapp.model.dto.response.RequestResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    RequestResponseDto toDto(Request request);
}
