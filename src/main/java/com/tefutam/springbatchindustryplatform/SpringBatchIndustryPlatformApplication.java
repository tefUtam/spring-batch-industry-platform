package com.tefutam.springbatchindustryplatform;

import com.tefutam.springbatchindustryplatform.config.IndustryJobProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(IndustryJobProperties.class)
public class SpringBatchIndustryPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchIndustryPlatformApplication.class, args);
    }
}
