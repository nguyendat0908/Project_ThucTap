package com.example.Project_Jobhunter.dto.response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import com.example.Project_Jobhunter.util.constant.LevelEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResJobDTO {

    private int id;
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
        private int id;
        private String name;
    }
}
