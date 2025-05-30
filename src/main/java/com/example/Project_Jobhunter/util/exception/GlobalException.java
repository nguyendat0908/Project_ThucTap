package com.example.Project_Jobhunter.util.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.Project_Jobhunter.dto.response.ResponseDTO;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = { IdInvalidException.class,
            UsernameNotFoundException.class, IllegalArgumentException.class })
    public ResponseEntity<ResponseDTO<Object>> handleIdException(Exception ex) {
        ResponseDTO<Object> res = new ResponseDTO<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getMessage());
        res.setMessage("Xảy ra ngoại lệ...");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseDTO<Object>> handleBadCredentials(BadCredentialsException ex) {
        ResponseDTO<Object> res = new ResponseDTO<Object>();
        res.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        res.setError(ex.getMessage());
        res.setMessage("Tên người dùng hoặc mật khẩu không đúng!");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
    }

    // Handle 404 exception
    @ExceptionHandler(value = {
            NoResourceFoundException.class,
    })
    public ResponseEntity<ResponseDTO<Object>> handleNotFoundException(Exception ex) {
        ResponseDTO<Object> res = new ResponseDTO<Object>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setError(ex.getMessage());
        res.setMessage("404 không tìm thấy. URL có thể không tồn tại...");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    // Handle validation exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<Object>> validationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();

        ResponseDTO<Object> res = new ResponseDTO<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getBody().getDetail());

        List<String> errors = fieldErrors.stream().map(f -> f.getDefaultMessage()).collect(Collectors.toList());
        res.setMessage(errors.size() > 1 ? errors : errors.get(0));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = { StorageException.class })
    public ResponseEntity<ResponseDTO<Object>> handleFileUploadException(Exception ex) {
        ResponseDTO<Object> res = new ResponseDTO<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getMessage());
        res.setMessage("Xảy ra ngoại lệ...");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = { PermissionException.class })
    public ResponseEntity<ResponseDTO<Object>> handlePermissionException(Exception ex) {
        ResponseDTO<Object> res = new ResponseDTO<Object>();
        res.setStatusCode(HttpStatus.FORBIDDEN.value());
        res.setError(ex.getMessage());
        res.setMessage("Forbidden...");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
    }

}
