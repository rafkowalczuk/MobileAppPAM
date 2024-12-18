package com.example.recipesapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FavouriteBadRequestException extends RuntimeException {

    public FavouriteBadRequestException() {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad favourite request");
    }

}
