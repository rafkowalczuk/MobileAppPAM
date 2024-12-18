package com.example.recipesapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RecipeBadRequestException extends RuntimeException {

    public RecipeBadRequestException() {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad recipe request");
    }

}
