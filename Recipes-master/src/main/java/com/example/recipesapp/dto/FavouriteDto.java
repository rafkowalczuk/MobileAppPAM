package com.example.recipesapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FavouriteDto {
    private Long recipeId;
    private LocalDateTime date;
}
