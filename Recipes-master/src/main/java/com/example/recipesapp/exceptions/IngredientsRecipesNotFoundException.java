package com.example.recipesapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class IngredientsRecipesNotFoundException extends RuntimeException {

    public IngredientsRecipesNotFoundException() {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "IngredientsRecipes not found");
    }
}
