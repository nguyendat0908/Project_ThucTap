package com.example.Project_Jobhunter.config.dateTime;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Formatting configuration for date and time in the application
// This configuration uses ISO format for date and time serialization/deserialization
@Configuration
public class ConfigFormatDateTime implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();

        // Formatter cho LocalDateTime, LocalDate, LocalTime với múi giờ Việt Nam
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
                .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));

        registrar.setDateTimeFormatter(dateTimeFormatter); // Áp dụng cho LocalDateTime
        registrar.setDateFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        registrar.setTimeFormatter(DateTimeFormatter.ofPattern("HH:mm"));

        registrar.registerFormatters(registry);
    }
}
