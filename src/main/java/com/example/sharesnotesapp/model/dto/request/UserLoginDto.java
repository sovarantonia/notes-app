package com.example.sharesnotesapp.model.dto.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserLoginDto {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 7)
    private String password;
}
