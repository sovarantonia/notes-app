package com.example.sharesnotesapp.model.dto.mapper;

import com.example.sharesnotesapp.model.Request;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.response.RequestResponseDto;
import com.example.sharesnotesapp.model.dto.response.UserResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    RequestResponseDto toDto(Request request);
}
