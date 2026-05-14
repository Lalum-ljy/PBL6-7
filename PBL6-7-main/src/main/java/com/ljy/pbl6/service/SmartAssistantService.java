package com.ljy.pbl6.service;

import com.ljy.pbl6.common.Response;
import com.ljy.pbl6.controller.ActivityController;
import com.ljy.pbl6.dto.PageResponse;
import com.ljy.pbl6.entity.Activity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SmartAssistantService {

    private final ActivityService activityService;
    private final ModelEngineService modelEngineService;
    private final ActivityController activityController;

    public String processRequest(String request) {
        // 预处理请求
        request = request.trim();
        System.out.println("接收到请求: " + request);
        
        // 让大模型解析用户意图并转译成固定指令
        String command = getCommandFromModel(request);
        System.out.println("大模型生成的指令: " + command);
        
        // 根据指令执行相应的操作
        return executeCommand(command, request);
    }

    private String getCommandFromModel(String request) {
        // 构建提示词，让大模型解析用户意图并生成固定格式的指令
        String prompt = "请严格按照以下要求分析用户请求并生成指令：\n\n" +
                "## 指令格式\n" +
                "1. 查询活动：必须返回 QUERY_ACTIVITIES\n" +
                "2. 创建活动：必须返回 CREATE_ACTIVITY\n" +
                "3. 其他请求：必须返回 OTHER\n\n" +
                "## 识别规则\n" +
                "- 查询活动：包含'有几个活动'、'活动数量'、'查询活动'、'活动列表'、'有什么活动'等关键词\n" +
                "- 创建活动：包含'创建活动'、'新活动'、'添加活动'等关键词\n" +
                "- 其他：不符合上述规则的请求\n\n" +
                "## 注意事项\n" +
                "1. 只返回指令，不要添加任何解释、SQL语句或其他内容\n" +
                "2. 指令必须是大写字母，不能包含其他字符\n" +
                "3. 严格按照上述格式返回，不要有任何偏差\n\n" +
                "用户请求：" + request + "\n" +
                "指令：";
        
        // 调用大模型获取指令
        String response = modelEngineService.chat(prompt);
        System.out.println("大模型原始响应: " + response);
        
        // 提取指令并标准化
        String command = response.trim();
        
        // 验证指令格式
        if (command.equals("QUERY_ACTIVITIES") || command.equals("CREATE_ACTIVITY") || command.equals("OTHER")) {
            return command;
        } else {
            // 如果指令格式不正确，默认返回OTHER
            System.out.println("指令格式不正确，默认返回OTHER");
            return "OTHER";
        }
    }

    private String executeCommand(String command, String originalRequest) {
        if (command.equals("QUERY_ACTIVITIES")) {
            return handleQueryRequest();
        } else if (command.startsWith("CREATE_ACTIVITY")) {
            return handleCreateRequest(command, originalRequest);
        } else {
            // 其他类型的请求，直接调用大模型
            return modelEngineService.chat(originalRequest);
        }
    }

    private String handleQueryRequest() {
        try {
            // 调用控制层接口获取活动列表
            Response<PageResponse<Activity>> response = activityController.findAll(1, 100);
            if (response.getCode() == 200 && response.getData() != null) {
                PageResponse<Activity> pageResponse = response.getData();
                List<Activity> activities = pageResponse.getData();
                int count = activities.size();
                
                if (count == 0) {
                    return "目前没有活动。";
                } else {
                    StringBuilder result = new StringBuilder("目前有 " + count + " 个活动：\n");
                    for (Activity activity : activities) {
                        result.append("- " + activity.getActivityName() + " (" + activity.getStartTime() + " 至 " + activity.getEndTime() + ")\n");
                    }
                    return result.toString();
                }
            } else {
                return "查询活动失败：" + response.getMessage();
            }
        } catch (Exception e) {
            return "查询活动时出错：" + e.getMessage();
        }
    }

    private String handleCreateRequest(String command, String originalRequest) {
        try {
            // 让大模型提取活动信息
            String prompt = "请从用户请求中提取活动信息，格式化为JSON：\n" +
                    "{\"name\": \"活动名称\", \"desc\": \"活动描述\", \"startTime\": \"开始时间(yyyy-MM-dd HH:mm:ss)\", \"endTime\": \"结束时间(yyyy-MM-dd HH:mm:ss)\"}\n\n" +
                    "用户请求：" + originalRequest + "\n" +
                    "请只返回JSON，不要添加任何解释或其他内容。";
            
            String jsonResponse = modelEngineService.chat(prompt);
            System.out.println("大模型提取的活动信息: " + jsonResponse);
            
            // 解析JSON
            org.json.JSONObject jsonObject = new org.json.JSONObject(jsonResponse);
            String activityName = jsonObject.getString("name");
            String activityDesc = jsonObject.optString("desc", "");
            String startTimeStr = jsonObject.getString("startTime");
            String endTimeStr = jsonObject.getString("endTime");
            String creator = "system";

            // 验证必填字段
            if (activityName == null || activityName.isEmpty() || startTimeStr == null || startTimeStr.isEmpty() || endTimeStr == null || endTimeStr.isEmpty()) {
                return "创建活动需要提供活动名称、开始时间和截止时间。";
            }

            // 解析时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
            LocalDateTime endTime = LocalDateTime.parse(endTimeStr, formatter);

            // 创建活动对象
            Activity activity = new Activity();
            activity.setActivityName(activityName);
            activity.setActivityDesc(activityDesc);
            activity.setStartTime(startTime);
            activity.setEndTime(endTime);
            activity.setCreator(creator);

            // 调用控制层接口创建活动
            Response<Activity> response = activityController.create(activity);
            if (response.getCode() == 200 && response.getData() != null) {
                Activity createdActivity = response.getData();
                return "活动创建成功！活动名称：" + createdActivity.getActivityName() + "，ID：" + createdActivity.getId();
            } else {
                return "创建活动失败：" + response.getMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "创建活动时出错：" + e.getMessage();
        }
    }
}
