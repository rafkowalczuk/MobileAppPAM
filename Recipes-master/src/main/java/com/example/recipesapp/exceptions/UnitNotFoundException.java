package com.example.recipesapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnitNotFoundException extends RuntimeException {

    public UnitNotFoundException() {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Unit not found");
    }
}
