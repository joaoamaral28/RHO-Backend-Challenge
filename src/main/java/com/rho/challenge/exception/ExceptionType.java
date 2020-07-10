package com.rho.challenge.exception;

/**
 * Represents the type of a CustomException
 */
public enum ExceptionType {

    NOT_FOUND_EXCEPTION("not.found"),
    STORAGE_FAILED_EXCEPTION("storage.fail"),
    RETRIEVE_FAILED_EXCEPTION("storage.retrive");
    // ...

    String value;

    ExceptionType(String value){
        this.value = value;
    }

    String getValue(){
        return this.value;
    }

}
