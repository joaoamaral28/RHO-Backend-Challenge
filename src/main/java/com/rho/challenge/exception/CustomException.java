package com.rho.challenge.exception;

import java.text.MessageFormat;

/**
 * Helper class to generate custom exceptions with increased modularity
 * Adapted from https://github.com/khandelwal-arpit/springboot-starterkit/tree/master/src/main/java/com/starterkit/springboot/brs/exception
 */
public class CustomException extends Exception{

    String error;

    /**
     * Returns new CustomException based on EntityType, ExceptionType and exception error message
     *
     * @param entityType The entity that produced the exception
     * @param exceptionType The type of exception
     * @param error The error message associated with the exception
     * @return CustomException
     */
    public static CustomException throwException(EntityType entityType, ExceptionType exceptionType, String error) {
        String messageTemplate = generateMessageTemplate(entityType, exceptionType);
        String messageVerbose = messageTemplate + "=" + error;
        return new CustomException(messageVerbose);
    }

    private CustomException(String error) {
        this.error = error;
    }

    private static String generateMessageTemplate(EntityType entityType, ExceptionType exceptionType){
        return entityType.name().concat(".").concat(exceptionType.getValue()).toLowerCase();
    }

    @Override
    public String getMessage(){
        return this.error;
    }

}
