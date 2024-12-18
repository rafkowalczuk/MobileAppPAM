package com.example.recipesapp.controller;

import com.example.recipesapp.dto.CommentDto;
import com.example.recipesapp.dto.IngredientsRecipesDto;
import com.example.recipesapp.entity.*;
import com.example.recipesapp.exceptions.NotAnAuthorException;
import com.example.recipesapp.service.IngredientsRecipesService;
import com.example.recipesapp.service.IngredientsService;
import com.example.recipesapp.service.RecipeService;
import com.example.recipesapp.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/ingredients/recipes")
public class IngredientsRecipesController {
    private final IngredientsRecipesService ingredientsRecipesService;
    private final RecipeService recipeService;
    private final IngredientsService ingredientsService;
    private final UnitService unitService;

    @GetMapping("/all")
    public ResponseEntity<List<IngredientsRecipes>> getAllIngredientsRecipes() {
        List<IngredientsRecipes> allIngredientsRecipes = ingredientsRecipesService.findAllIngredientsRecipes();
        return new ResponseEntity<>(allIngredientsRecipes, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<IngredientsRecipes> postRating(@RequestBody IngredientsRecipesDto ingredientsRecipesDto) {
        Recipe recipe = recipeService.getRecipe(ingredientsRecipesDto.getRecipeId());
        Ingredient ingredient = ingredientsService.getIngredientWithId(ingredientsRecipesDto.getIngredientId());
        Unit unit = unitService.getUnitWithId(ingredientsRecipesDto.getUnitId());

        IngredientsRecipes newIngredientsRecipes = new IngredientsRecipes();
        newIngredientsRecipes.setIngredient(ingredient);
        newIngredientsRecipes.setRecipe(recipe);
        newIngredientsRecipes.setUnit(unit);
        newIngredientsRecipes.setAmount(ingredientsRecipesDto.getAmount());

        IngredientsRecipes ingredientsRecipes = ingredientsRecipesService.addIngredientsRecipes(newIngredientsRecipes);
        return new ResponseEntity<>(ingredientsRecipes, HttpStatus.OK);
    }

    @GetMapping("/id/{recipeId}")
    public ResponseEntity<List<IngredientsRecipes>> getAllIngredientsRecipesByRecipeId(@PathVariable Long recipeId) {
        List<IngredientsRecipes> ingredientsRecipes = ingredientsRecipesService.findAllIngredientsRecipesWithRecipe(recipeId);
        return new ResponseEntity<>(ingredientsRecipes, HttpStatus.OK);
    }

    @DeleteMapping(value = "{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public  ResponseEntity<Void> deleteIngredientsRecipes(@PathVariable final Integer id, @AuthenticationPrincipal UserDetails details) {
        IngredientsRecipes ingredientsRecipesToDelete = ingredientsRecipesService.findIngredientsRecipesWithId(id);
        if (!ingredientsRecipesToDelete.getRecipe().getOwner().getEmail().equals(details.getUsername()) && !details.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))){
            throw new NotAnAuthorException();
        } else {
            ingredientsRecipesService.deleteIngredientsRecipes(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PutMapping(value = "{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> editComment(@PathVariable final Integer id, @Valid @RequestBody IngredientsRecipesDto ingredientsRecipesDto,
                                            @AuthenticationPrincipal UserDetails details) {
        IngredientsRecipes ingredientsRecipesToEdit = ingredientsRecipesService.findIngredientsRecipesWithId(id);

        if (!ingredientsRecipesToEdit.getRecipe().getOwner().getEmail().equals(details.getUsername())){
            throw new NotAnAuthorException();
        } else {
            ingredientsRecipesToEdit.setAmount(ingredientsRecipesDto.getAmount());
            ingredientsRecipesService.editIngredientsRecipes(ingredientsRecipesToEdit);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
