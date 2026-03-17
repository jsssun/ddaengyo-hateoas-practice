package dev.ddaengyo.auth.dto;

import dev.ddaengyo.entity.Role;
import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class SignUpRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    @Pattern(regexp = "^\\d{10,11}$")
    private String phone;

    @NotNull
    private Role role;

    private String currentAddress;
}