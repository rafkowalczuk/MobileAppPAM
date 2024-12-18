package com.example.recipesapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FavouriteNotFoundException extends RuntimeException {

    public FavouriteNotFoundException() {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Favourite not found");
    }
}
