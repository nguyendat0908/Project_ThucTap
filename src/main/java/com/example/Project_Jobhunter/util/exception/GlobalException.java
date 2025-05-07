package com.example.Project_Jobhunter.util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.Project_Jobhunter.dto.response.ResponseDTO;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = { IdInvalidException.class })
    public ResponseEntity<ResponseDTO<Object>> handleIdException(Exception ex) {
        ResponseDTO<Object> res = new ResponseDTO<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getMessage());
        res.setMessage("Exception occurs...");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
}
