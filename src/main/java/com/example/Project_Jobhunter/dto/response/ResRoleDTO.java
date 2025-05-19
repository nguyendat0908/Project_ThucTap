package com.example.Project_Jobhunter.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResRoleDTO {

    private UUID id;
    private String name;
    private boolean active;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

    private List<String> permissions;
}
