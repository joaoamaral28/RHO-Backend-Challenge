package com.rho.challenge.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration class for the server WebSocket and its underlying topics.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        // prefix "/monitor" for messages bound to methods annotated with @MessageMapping
        config.setApplicationDestinationPrefixes("/monitor");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /* registers "/gs-guide-websocket" enabling SockJS fallback options so that alternate transports
         *  can be used if WebSocket is not available. SockJS client will attempt to connect to "/gs-guide-websocket"
         *  and use the best available transport (websocket, xhr-streaming, xhr-polling, and so on). */
        registry.addEndpoint("/gs-guide-websocket").withSockJS();
    }

}

