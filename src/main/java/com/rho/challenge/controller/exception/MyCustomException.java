package com.rho.challenge.controller.exception;

import org.springframework.validation.ObjectError;

import java.util.List;

public class MyCustomException extends Exception{

    List<ObjectError> errors;

    public static MyCustomException createWith(List<ObjectError> errors) {
        return new MyCustomException(errors);
    }

    private MyCustomException(List<ObjectError> errors) {
        this.errors = errors;
    }

    public List<ObjectError> getErrors() {
        return errors;
    }

}
