package com.example.recipesapp.service;

import com.example.recipesapp.entity.IngredientsRecipes;
import com.example.recipesapp.exceptions.CommentBadRequestException;
import com.example.recipesapp.exceptions.IngredientsRecipesBadRequestException;
import com.example.recipesapp.exceptions.TagNotFoundException;
import com.example.recipesapp.repositories.IngredientsRecipesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientsRecipesService {
    private final IngredientsRecipesRepository ingredientsRecipesRepository;

    public List<IngredientsRecipes> findAllIngredientsRecipes() {
        return ingredientsRecipesRepository.findAll();
    }

    public IngredientsRecipes findIngredientsRecipesWithId(Integer id) {
        return ingredientsRecipesRepository.findById(id).orElseThrow(TagNotFoundException::new);
    }

    public List<IngredientsRecipes> findAllIngredientsRecipesWithRecipe(Long recipeId) {
        return ingredientsRecipesRepository.findAllByRecipeId(recipeId);
    }

    public IngredientsRecipes addIngredientsRecipes(IngredientsRecipes newIngredientsRecipes) {
        try {
            return ingredientsRecipesRepository.save(newIngredientsRecipes);
        } catch (Exception exception) {
            throw new IngredientsRecipesBadRequestException();
        }
    }

    @Transactional
    public void deleteIngredientsRecipes(Integer id) {
        try {
            ingredientsRecipesRepository.delete(findIngredientsRecipesWithId(id));
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete IngredientsRecipes with ID " + id, e);
        }
    }

    public IngredientsRecipes editIngredientsRecipes(@Valid IngredientsRecipes newIngredientsRecipes) {
        try {
            ingredientsRecipesRepository.save(newIngredientsRecipes);
            return newIngredientsRecipes;
        } catch (RuntimeException exception) {
            throw new CommentBadRequestException();
        }
    }

}
