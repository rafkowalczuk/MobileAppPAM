package com.example.recipesapp.controller;

import com.example.recipesapp.entity.Ingredient;
import com.example.recipesapp.exceptions.NotAnAuthorException;
import com.example.recipesapp.service.IngredientsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ingredient")
public class IngredientController {
    private final IngredientsService ingredientsService;

    @GetMapping("/all")
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        List<Ingredient> allIngredients = ingredientsService.findAllIngredients();
        return new ResponseEntity<>(allIngredients, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<Ingredient> addIngredient(@RequestBody Ingredient ingredient) {
        Ingredient newIngredient = ingredientsService.saveIngredient(ingredient);
        return new ResponseEntity<>(newIngredient, HttpStatus.OK);
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<Ingredient> getIngredient(@PathVariable final Integer id) {
        Ingredient ingredient = ingredientsService.getIngredientWithId(id);
        return new ResponseEntity<>(ingredient, HttpStatus.OK);
    }

    @DeleteMapping(value = "{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public  ResponseEntity<Void> deleteIngredient(@PathVariable final Integer id, @AuthenticationPrincipal UserDetails details) {
        Ingredient ingredientToDelete = ingredientsService.getIngredientWithId(id);
        if (!details.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))){
            throw new NotAnAuthorException();
        } else {
            ingredientsService.deleteIngredient(ingredientToDelete);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
