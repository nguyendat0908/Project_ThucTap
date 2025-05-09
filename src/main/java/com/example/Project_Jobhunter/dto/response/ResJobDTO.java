package com.example.Project_Jobhunter.dto.response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.example.Project_Jobhunter.util.constant.LevelEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResJobDTO {

    private UUID id;
    private String name;
    private String location;
    private double salary;
    private int quantity;
    private boolean active;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LevelEnum level;
    private Instant createdAt;
    private Instant updatedAt;

    private List<String> skills;
    private CompanyJob companyJob;

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompanyJob {
        private UUID id;
        private String name;
    }
}
