package com.example.recipesapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class IngredientsRecipesBadRequestException extends RuntimeException {

    public IngredientsRecipesBadRequestException() {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "IngredientsRecipes unit request");
    }

}
