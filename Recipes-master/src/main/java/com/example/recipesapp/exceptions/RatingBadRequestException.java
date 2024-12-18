package com.example.recipesapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RatingBadRequestException extends RuntimeException {

    public RatingBadRequestException() {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad rating request");
    }

}
