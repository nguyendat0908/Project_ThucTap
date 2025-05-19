package com.example.Project_Jobhunter.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO<T> {
    private int statusCode;
    private String error;
    
    // Message can be a string or an object, so we use Object type
    private Object message;
    private T data;
}
