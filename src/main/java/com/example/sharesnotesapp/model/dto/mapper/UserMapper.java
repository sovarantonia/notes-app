package com.example.sharesnotesapp.model.dto.mapper;

import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.response.UserInfoDto;
import com.example.sharesnotesapp.model.dto.response.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    default UserResponseDto toDto(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDto.UserResponseDtoBuilder dtoBuilder = UserResponseDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail());

        if (user.getFriendList() != null) {
            List<UserInfoDto> friendDtos = toUserDtoList(user.getFriendList());
            dtoBuilder.friends(friendDtos);
        }

        return dtoBuilder.build();
    }
    List<UserResponseDto> toDtoList(List<User> users);
    @Mapping(source = "id", target = "id")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "email", target = "email")
    UserInfoDto toUserInfoDto(User user);

    List<UserInfoDto> toUserDtoList(List<User> users);
}
