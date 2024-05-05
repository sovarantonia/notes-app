package com.example.sharesnotesapp.model.dto.mapper;

import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.response.UserResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toDto(User user);
}
