package com.example.recipesapp.repositories;

import com.example.recipesapp.entity.IngredientsRecipes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientsRecipesRepository  extends JpaRepository<IngredientsRecipes, Integer> {

    @Query("SELECT ir FROM IngredientsRecipes ir WHERE ir.recipe.id = :recipeId")
    List<IngredientsRecipes> findAllByRecipeId(@Param("recipeId") Long recipeId);
}
