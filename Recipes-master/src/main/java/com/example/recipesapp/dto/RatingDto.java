package com.example.recipesapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RatingDto {
    private Long recipeId;
    private Integer rating;
    private LocalDateTime date;
}
