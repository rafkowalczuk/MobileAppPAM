package com.example.recipesapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TagBadRequestException extends RuntimeException {

    public TagBadRequestException() {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad tag request");
    }

}
