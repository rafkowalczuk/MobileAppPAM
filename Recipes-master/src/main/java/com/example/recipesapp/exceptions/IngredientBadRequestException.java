package com.example.recipesapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class IngredientBadRequestException extends RuntimeException {

    public IngredientBadRequestException() {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Ingredient unit request");
    }

}
