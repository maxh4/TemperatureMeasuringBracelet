package com.futongware.temperaturemeasuringbracelet.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //将clientMessage注册为STOMP的一个端点
        //客户端在订阅或发布消息到目的路径前，要连接该端点
        //setAllowedOrigins允许所有域连接，否则浏览器可能报403错误
        registry.addEndpoint("/clientMessage").setAllowedOrigins("*").withSockJS();
    }

}