package com.rho.challenge.model;

import java.util.List;

/* Class representing the verbose errors generated due to an exception at the controller level */
public class ErrorMessage {

    private List<String> errors;

    public ErrorMessage(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

}
