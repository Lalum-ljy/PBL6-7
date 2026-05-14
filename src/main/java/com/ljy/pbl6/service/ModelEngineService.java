package com.ljy.pbl6.service;

import com.ljy.pbl6.config.ModelEngineConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ModelEngineService {

    private final ModelEngineConfig modelEngineConfig;
    private final RestTemplate restTemplate;

    public String chat(String prompt) {
        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + modelEngineConfig.getApiKey());

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelEngineConfig.getModelName());
        requestBody.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("temperature", 0.7);

        // 发送请求
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                modelEngineConfig.getApiUrl(),
                HttpMethod.POST,
                entity,
                Map.class
        );

        // 解析响应
        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null) {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            if (!choices.isEmpty()) {
                Map<String, Object> choice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) choice.get("message");
                return (String) message.get("content");
            }
        }
        return "Error: No response from model";
    }
}
