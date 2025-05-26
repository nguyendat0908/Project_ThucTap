package com.example.Project_Jobhunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project_Jobhunter.service.EmailService;
import com.example.Project_Jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/email")
    @ApiMessage("Gửi email thành công!")
    public String getMethodName() {
        this.emailService.sendEmailFromTemplate("nguyendat090803@gmail.com", "Test send email", "job");
        return "OK";
    }
}
