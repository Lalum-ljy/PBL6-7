package com.ljy.pbl6.controller;

import com.ljy.pbl6.common.Response;
import com.ljy.pbl6.service.ModelEngineService;
import com.ljy.pbl6.service.SmartAssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final SmartAssistantService smartAssistantService;
    private final ModelEngineService modelEngineService;

    @PostMapping
    public Response<String> chat(@RequestBody Map<String, Object> request) {
        // 检查请求类型
        String type = (String) request.get("type");
        String message = (String) request.get("message");
        
        if (message == null || message.isEmpty()) {
            return Response.error(400, "请提供消息内容");
        }
        
        // 根据类型选择处理方式
        String response;
        if ("direct".equals(type)) {
            // 直接调用大模型
            response = modelEngineService.chat(message);
        } else {
            // 使用智能助手
            response = smartAssistantService.processRequest(message);
        }
        
        return Response.success(response);
    }
}
