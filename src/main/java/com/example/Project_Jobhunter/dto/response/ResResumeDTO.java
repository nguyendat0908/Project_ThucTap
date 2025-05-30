package com.example.Project_Jobhunter.dto.response;

import java.time.Instant;

import com.example.Project_Jobhunter.util.constant.StatusEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResResumeDTO {

    private int id;
    private String email;
    private String url;
    private StatusEnum status;
    private Instant createdAt;
    private Instant updatedAt;

    private UserResume userResume;
    private JobResume jobResume;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResume {
        private int id;
        private String name;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobResume {
        private int id;
        private String name;
    }
}
