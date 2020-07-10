package com.rho.challenge.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rho.challenge.exception.CustomException;
import com.rho.challenge.model.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

/**
 *  Global exception handler class for the application controller
 *  */
@ControllerAdvice
public class GlobalExceptionHandler{

    @MessageExceptionHandler(CustomException.class)
    @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
    @SendTo("/topic/notifications")
    public final ErrorMessage handleCustomException(Exception ex){
        ErrorMessage error = new ErrorMessage(ex.getMessage());
        System.out.println(error.getMessage());
        return error;
    }

    @MessageExceptionHandler(JsonProcessingException.class)
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    @SendTo("/topic/notifications")
    public final ErrorMessage handleJsonProcessingException(Exception ex){
        ErrorMessage error = new ErrorMessage("Invalid JSON request");
        // System.out.println(ex.getLocalizedMessage());
        return error;
    }

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    @SendTo("/topic/notifications")
    public final ErrorMessage handleMethodArgumentNotValidException(Exception ex){
        ErrorMessage error = new ErrorMessage("Invalid JSON request");
        // System.out.println(ex.getLocalizedMessage());
        return error;
    }
}
