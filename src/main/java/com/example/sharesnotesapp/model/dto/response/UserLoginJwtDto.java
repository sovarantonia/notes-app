package com.example.sharesnotesapp.model.dto.response;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class UserLoginJwtDto {
    private String email;
    private String tokenValue;
}
