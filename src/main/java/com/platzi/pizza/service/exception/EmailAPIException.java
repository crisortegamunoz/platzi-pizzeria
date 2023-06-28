package com.platzi.pizza.service.exception;

public class EmailAPIException extends RuntimeException {

    public EmailAPIException() {
        super("Error enviando el email");
    }

}
