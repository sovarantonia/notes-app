package com.example.sharesnotesapp.model.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserNameDto {
    @NotBlank(message = "First name should not be empty")
    private String firstName = "";
    @NotBlank(message = "Last name should not be empty")
    private String lastName = "";
}
