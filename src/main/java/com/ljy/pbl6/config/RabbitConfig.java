package com.ljy.pbl6.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class RabbitConfig {

    public static final String ACTIVITY_STATUS_QUEUE = "activity.status.queue";
    public static final String ACTIVITY_STATUS_EXCHANGE = "activity.status.exchange";
    public static final String ACTIVITY_STATUS_ROUTING_KEY = "activity.status.update";

    @Bean
    public Queue activityStatusQueue() {
        return new Queue(ACTIVITY_STATUS_QUEUE, true);
    }

    @Bean
    public DirectExchange activityStatusExchange() {
        return new DirectExchange(ACTIVITY_STATUS_EXCHANGE);
    }

    @Bean
    public Binding activityStatusBinding(Queue activityStatusQueue, DirectExchange activityStatusExchange) {
        return BindingBuilder.bind(activityStatusQueue)
                .to(activityStatusExchange)
                .with(ACTIVITY_STATUS_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        // 创建ObjectMapper并注册Java 8日期时间模块
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        
        // 创建消息转换器
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        
        // 设置类映射器
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("com.ljy.pbl6.dto");
        converter.setClassMapper(classMapper);
        
        return converter;
    }
}