package com.example.Project_Jobhunter.dto.response;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
public class ResLoginDTO {

    private UserLogin userLogin;

    // @JsonProperty("accessToken")
    private String accessToken;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLogin {
        private UUID id;
        private String email;
        private String name;
        private RoleUserLogin roleUserLogin;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleUserLogin {
        private String name;
        private List<String> permissions;
    }

}
