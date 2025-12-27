package com.mediaservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class GeminiConfig {

    @Value("${gemini.api.key:mock-api-key}")
    private String apiKey;

    @Value("${gemini.model.name:gemini-1.5-flash}")
    private String modelName;

    // TODO: 실제 Gemini API 연동 시 GenerativeModel Bean 반환
    // 현재는 Mock 구현을 사용하므로 Bean을 생성하지 않음
    
    public void logConfig() {
        log.info("🤖 Mock Gemini API 설정 완료 - Model: {}, API Key: {}", 
                modelName, apiKey.substring(0, Math.min(apiKey.length(), 10)) + "...");
    }
}
