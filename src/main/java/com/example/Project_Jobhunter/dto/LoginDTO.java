package com.example.Project_Jobhunter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginDTO {

    @NotBlank(message = "Username is not empty!")
    private String username;

    @NotBlank(message = "Password is not empty!")
    private String password;
}
