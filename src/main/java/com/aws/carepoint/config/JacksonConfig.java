package com.aws.carepoint.config;

import com.fasterxml.jackson.databind.ObjectMapper;
<<<<<<< HEAD
=======
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
>>>>>>> df4d74ac3d213e01b1040c5f42d112331dd328d1
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
<<<<<<< HEAD
        return new ObjectMapper();
    }
}
=======
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); //JavaTimeModule 등록 Jackson이 LocalDate를 변환할 수 있도록 설정 추가!
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO 8601 포맷 유지
        return objectMapper;
    }
}
>>>>>>> df4d74ac3d213e01b1040c5f42d112331dd328d1
