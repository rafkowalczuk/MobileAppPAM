package com.example.recipesapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CommentBadRequestException extends RuntimeException {

    public CommentBadRequestException() {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad comment request");
    }

}
