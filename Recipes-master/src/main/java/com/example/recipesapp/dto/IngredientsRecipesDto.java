package com.example.recipesapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngredientsRecipesDto {
    private Long recipeId;
    private Integer ingredientId;
    private Integer unitId;
    private Double amount;

}
