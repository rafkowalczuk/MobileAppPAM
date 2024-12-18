package com.example.recipesapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CommentNotFoundException extends RuntimeException {

    public CommentNotFoundException() {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Comment not found");
    }
}
