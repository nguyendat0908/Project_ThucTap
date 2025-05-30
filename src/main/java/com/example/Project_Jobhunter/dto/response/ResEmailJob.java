package com.example.Project_Jobhunter.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResEmailJob {

    private String name;
    private double salary;
    private CompanyEmail company;
    private List<SkillEmail> skills;

    @Setter
    @Getter
    @AllArgsConstructor
    public static class CompanyEmail {
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class SkillEmail {
        private String name;
    }

}
