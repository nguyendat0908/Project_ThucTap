package com.example.Project_Jobhunter.config.dateTime;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class InstantSerializer extends JsonSerializer<Instant> {

    @Override
    public void serialize(Instant instant, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        // Format Instant theo múi giờ Việt Nam
        String formattedInstant = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                .withZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                .format(instant);

        jsonGenerator.writeString(formattedInstant); // Ghi kết quả đã format vào JSON
    }
}
