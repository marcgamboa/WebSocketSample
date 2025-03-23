package com.mgamboa.config;

import com.mgamboa.handler.ChatWSHandler;
import com.mgamboa.handler.GameWSHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new GameWSHandler(), "/game")
                .addHandler(new ChatWSHandler(), "/chat")
                .setAllowedOrigins("*"); // Allow all origins (CORS)
    }
}
