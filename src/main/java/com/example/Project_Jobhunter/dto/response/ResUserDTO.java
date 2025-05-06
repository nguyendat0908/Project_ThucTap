package com.example.Project_Jobhunter.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.example.Project_Jobhunter.util.constant.GenderEnum;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResUserDTO {

    private UUID id;
    private String name;
    private String email;
    private String address;
    private boolean active;
    private int age;
    private String refreshToken;
    private String avatar;
    private GenderEnum gender;
    private Instant createdAt;
    private Instant updatedAt;
}
