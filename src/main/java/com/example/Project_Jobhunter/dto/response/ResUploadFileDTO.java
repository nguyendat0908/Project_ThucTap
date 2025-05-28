package com.example.Project_Jobhunter.dto.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResUploadFileDTO {

    private String fileName;
    private Instant updatedAt;
}
