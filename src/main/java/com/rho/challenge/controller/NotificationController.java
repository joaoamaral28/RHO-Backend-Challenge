package com.rho.challenge.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rho.challenge.exception.CustomException;
import com.rho.challenge.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * The NotificationController class is the entry point of the application. It is responsible for
 * receiving clients bets via WebSocket messages and respond either with and acknowledge message
 * or a notification message.
 */

@Controller
public class NotificationController {

    private NotificationService notification_service;

    @Autowired
    public NotificationController(NotificationService notification_service){
        this.notification_service = notification_service;
    }

    @MessageMapping("/process_bet")
    @SendTo("/topic/notifications")
    public String processBet(@Payload @NonNull String message) throws CustomException, JsonProcessingException {
        return notification_service.processMessage(message);
    }

}
