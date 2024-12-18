package com.example.recipesapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RecipeNotFoundException extends RuntimeException {

    public RecipeNotFoundException() {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Recipe not found");
    }
}
