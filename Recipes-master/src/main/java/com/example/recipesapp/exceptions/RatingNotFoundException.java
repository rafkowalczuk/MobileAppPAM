package com.example.recipesapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RatingNotFoundException extends RuntimeException {

    public RatingNotFoundException() {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Rating not found");
    }
}
