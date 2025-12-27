package com.mediaservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    // 스케줄링 활성화를 위한 설정
    // @Scheduled 어노테이션을 사용할 수 있도록 함
}

