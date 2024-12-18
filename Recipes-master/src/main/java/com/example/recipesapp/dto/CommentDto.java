package com.example.recipesapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {
    private Long recipeId;
    private String comment;
    private LocalDateTime date;
}
