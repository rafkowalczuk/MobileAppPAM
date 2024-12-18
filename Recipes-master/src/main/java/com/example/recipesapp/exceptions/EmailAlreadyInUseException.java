package com.example.recipesapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

    public class EmailAlreadyInUseException extends RuntimeException{

        public EmailAlreadyInUseException() {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email Already In Use");
        }
    }
