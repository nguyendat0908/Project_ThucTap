package com.example.Project_Jobhunter.service;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.example.Project_Jobhunter.domain.Job;
import com.example.Project_Jobhunter.repository.JobRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final MailSender mailSender;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final JobRepository jobRepository;

    public EmailService(MailSender mailSender, JavaMailSender javaMailSender, SpringTemplateEngine templateEngine,
            JobRepository jobRepository) {
        this.mailSender = mailSender;
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.jobRepository = jobRepository;
    }

    public void sendEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("nguyendat090803@gmail.com");
        message.setSubject("Test Email");
        message.setText("This is a test email from JobHunter application.");
        this.mailSender.send(message);
    }

    public void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content, isHtml);
            this.javaMailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            System.out.println("Lỗi khi gửi email: " + e);
        }
    }

    public void sendEmailFromTemplate(String to, String subject, String templateName) {
        Context context = new Context();
        List<Job> jobs = this.jobRepository.findAll();
        String name = "Dat";
        context.setVariable("name", name);
        context.setVariable("jobs", jobs);

        String content = this.templateEngine.process(templateName, context);
        this.sendEmailSync(to, subject, content, false, true);
    }
}
