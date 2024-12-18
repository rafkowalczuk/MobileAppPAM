package com.example.recipesapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class IngredientNotFoundException extends RuntimeException {

    public IngredientNotFoundException() {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Ingredient not found");
    }
}
