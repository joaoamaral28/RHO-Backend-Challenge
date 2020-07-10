package com.rho.challenge.model;

/**
 * Class representing the verbose error message generated form an exception
 */
public class ErrorMessage {

    /**
     * Verbose message representing the occurred internal error
     */
    private String message;

    /**
     * Creates a new ErrorMessage with the given error message String
     * @param message String representing the error
     */
    public ErrorMessage(String message){
        this.message = message;
    }

    /**
     * Retrieves the error message
     * @return the error message String
     */
    public String getMessage() {
        return message;
    }

}
