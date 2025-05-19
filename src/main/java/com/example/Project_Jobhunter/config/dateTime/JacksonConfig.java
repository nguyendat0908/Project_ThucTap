package com.example.Project_Jobhunter.config.dateTime;

import java.time.Instant;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Tạo module và đăng ký InstantSerializer
        SimpleModule module = new SimpleModule();
        module.addSerializer(Instant.class, new InstantSerializer());

        // Thêm module vào ObjectMapper
        objectMapper.registerModule(module);
        objectMapper.registerModule(new JavaTimeModule());

        // Tránh serialize LocalDate thành timestamp (số milliseconds)
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return objectMapper;
    }
}
