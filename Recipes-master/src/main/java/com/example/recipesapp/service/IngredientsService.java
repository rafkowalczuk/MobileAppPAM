package com.example.recipesapp.service;

import com.example.recipesapp.entity.Ingredient;
import com.example.recipesapp.exceptions.IngredientBadRequestException;
import com.example.recipesapp.exceptions.IngredientNotFoundException;
import com.example.recipesapp.repositories.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientsService {
    private final IngredientRepository ingredientRepository;

    public List<Ingredient> findAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Ingredient getIngredientWithId(Integer id) {
        return ingredientRepository.findById(id).orElseThrow(IngredientNotFoundException::new);
    }

    public Ingredient saveIngredient(Ingredient newIngredient){
        try {
            return ingredientRepository.save(newIngredient);
        } catch (Exception exception) {
            throw new IngredientBadRequestException();
        }
    }

    @Transactional
    public void deleteIngredient(Ingredient ingredientToDelete) {
        try {
            ingredientRepository.delete(ingredientToDelete);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete ingredient with ID " + ingredientToDelete.getId(), e);
        }
    }
}
