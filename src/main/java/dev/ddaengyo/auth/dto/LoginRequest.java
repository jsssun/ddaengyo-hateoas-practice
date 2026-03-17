package dev.ddaengyo.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}