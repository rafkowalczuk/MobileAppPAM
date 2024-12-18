package com.example.recipesapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = EmailAlreadyInUseException.class)
    public ResponseEntity<Object> exception(EmailAlreadyInUseException exception) {
        return new ResponseEntity<>("Email Already In Use", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NotAnAuthorException.class)
    public ResponseEntity<Object> exception(NotAnAuthorException exception) {
        return new ResponseEntity<>("You are not an owner of the recipe", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = RecipeBadRequestException.class)
    public ResponseEntity<Object> exception(RecipeBadRequestException exception) {
        return new ResponseEntity<>("Bad recipe request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = RecipeNotFoundException.class)
    public ResponseEntity<Object> exception(RecipeNotFoundException exception) {
        return new ResponseEntity<>("Recipe not found", HttpStatus.NOT_FOUND);
    }
}
