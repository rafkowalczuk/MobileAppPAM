package com.example.recipesapp.repositories;

import com.example.recipesapp.entity.Recipe;
import com.example.recipesapp.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipesRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByCategoriesIgnoreCase(String category);
    List<Recipe> findByNameContainsIgnoreCase(String category);
    @Query("SELECT r.tags FROM Recipe r WHERE r.id = :recipeId")
    List<Tag> findTagsByRecipeId(@Param("recipeId") Long recipeId);


}
