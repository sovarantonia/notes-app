package com.example.sharesnotesapp.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserInfoDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
