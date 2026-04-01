package com.ljy.pbl6.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "mcp.model.deepseek-r1")
public class ModelEngineConfig {
    private String apiUrl;
    private String apiKey;
    private String modelName;
}
