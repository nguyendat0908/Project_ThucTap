package com.example.Project_Jobhunter.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResPaginationDTO {

    private Meta meta;
    private Object result;

    @Setter
    @Getter
    public static class Meta {
        private int page;
        private int pageSize;
        private int pages;
        private long total;
    }
}
