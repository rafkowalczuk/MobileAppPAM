package com.example.recipesapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TagNotFoundException extends RuntimeException {

    public TagNotFoundException() {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Tag not found");
    }
}
