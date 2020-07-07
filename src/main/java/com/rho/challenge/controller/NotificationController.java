package com.rho.challenge.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.rho.challenge.model.Bet;
import com.rho.challenge.model.Notification;
import com.rho.challenge.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    private NotificationService notification_service;

    @Autowired
    public NotificationController(NotificationService notification_service){
        this.notification_service = notification_service;
    }

    @MessageMapping("/process_bet")
    @SendTo("/topic/notifications")
    public String processBet(@Payload @NonNull String message){

        JsonObject json_message;
        try{
            json_message = new JsonParser().parse(message).getAsJsonObject();
        }catch (JsonSyntaxException e){
            return "Invalid JSON format";
        }

        Bet b;

        try{
            int account_id = json_message.get("account_id").getAsInt();
            double stake = json_message.get("stake").getAsDouble();
            b = new Bet(account_id, stake);
            System.out.println(b);
        }catch (NumberFormatException e) {
            return "Bet ID and Stake amount must be digits";
        }catch (IllegalArgumentException e){
            return e.getMessage();
        }

        Object ret = notification_service.processBet(b);

        System.out.println(ret);

        if(ret instanceof String){
            return (String) ret;
        }else if(ret instanceof Notification){
            try{
                return ((Notification) ret).toJSONString();
            }catch (Exception e){
                System.out.println("Failed to publish notification: " + ret);
                return "Error while publishing notification \n" + e;
            }
        }
        return "Error while processing bet";

    }

    /*
    @MessageExceptionHandler
    @SendTo("/topic/notifications")
    public String handleException(Throwable exception){
        return exception.getMessage();
    }
    */

}
