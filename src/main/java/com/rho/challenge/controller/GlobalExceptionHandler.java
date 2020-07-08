package com.rho.challenge.controller;

import com.rho.challenge.controller.exception.MyCustomException;
import com.rho.challenge.model.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import java.util.ArrayList;
import java.util.List;

/* Global exception handler class for the application controller */

@ControllerAdvice
public class GlobalExceptionHandler{

    @MessageExceptionHandler(MyCustomException.class)
    public final ResponseEntity<ErrorMessage> handleMyCustomException(Exception ex, String message){

        System.out.println(message);

        List<String> details = new ArrayList<>();
        details.add(ex.getLocalizedMessage());
        ErrorMessage error = new ErrorMessage(details);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(NullPointerException.class)
    public final ResponseEntity<ErrorMessage> handleNullPointerException(Exception ex, String message){

        System.out.println(message);

        List<String> details = new ArrayList<>();
        details.add(ex.getLocalizedMessage());
        ErrorMessage error = new ErrorMessage(details);

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    /*
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorMessage> handleAllExceptions(Exception ex, WebRequest request){
        List<String> details = new ArrayList<>();
        details.add(ex.getLocalizedMessage());
        ErrorMessage error = new ErrorMessage(details);

        System.out.println("AA");

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    */

}
