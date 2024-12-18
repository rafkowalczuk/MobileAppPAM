package com.example.recipesapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotAnAuthorException extends RuntimeException{

    public NotAnAuthorException() {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not an owner of the recipe");
    }

}
