package com.example.sharesnotesapp.model.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RequestRequestDto {
    @NotBlank(message = "Field cannot be empty")
    private Long senderId;
    @NotBlank(message = "Field cannot be empty")
    private String receiverEmail;
}
