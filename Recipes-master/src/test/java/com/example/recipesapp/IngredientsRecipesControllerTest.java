package com.example.recipesapp;

import com.example.recipesapp.controller.IngredientsRecipesController;
import com.example.recipesapp.dto.IngredientsRecipesDto;
import com.example.recipesapp.entity.*;
import com.example.recipesapp.exceptions.NotAnAuthorException;
import com.example.recipesapp.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IngredientsRecipesController.class)
@Import(TestSecurityConfig.class)
public class IngredientsRecipesControllerTest {


    @MockBean
    private IngredientsRecipesService ingredientsRecipesService;

    @MockBean
    private RecipeService recipeService;

    @MockBean
    private IngredientsService ingredientsService;

    @MockBean
    private UnitService unitService;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testGetAllIngredientsRecipes() throws Exception {
        when(ingredientsRecipesService.findAllIngredientsRecipes()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/ingredients/recipes/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testPostIngredientsRecipes() throws Exception {
        Recipe recipe = new Recipe();
        Ingredient ingredient = new Ingredient();
        Unit unit = new Unit();
        IngredientsRecipesDto ingredientsRecipesDto = new IngredientsRecipesDto();
        ingredientsRecipesDto.setRecipeId(1L);
        ingredientsRecipesDto.setIngredientId(1);
        ingredientsRecipesDto.setUnitId(1);
        ingredientsRecipesDto.setAmount(1.0);

        when(recipeService.getRecipe(1L)).thenReturn(recipe);
        when(ingredientsService.getIngredientWithId(1)).thenReturn(ingredient);
        when(unitService.getUnitWithId(1)).thenReturn(unit);
        when(ingredientsRecipesService.addIngredientsRecipes(any(IngredientsRecipes.class))).thenReturn(new IngredientsRecipes());

        mockMvc.perform(post("/api/ingredients/recipes/new")
                        .contentType("application/json")
                        .content("{\"recipeId\":1,\"ingredientId\":1,\"unitId\":1,\"amount\":1.0}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testGetAllIngredientsRecipesByRecipeId() throws Exception {
        when(ingredientsRecipesService.findAllIngredientsRecipesWithRecipe(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/ingredients/recipes/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    public void testDeleteIngredientsRecipes() throws Exception {
        IngredientsRecipes ingredientsRecipes = new IngredientsRecipes();
        Recipe recipe = new Recipe();
        User user = new User();
        user.setEmail("admin@example.com");
        recipe.setOwner(user);
        ingredientsRecipes.setRecipe(recipe);

        when(ingredientsRecipesService.findIngredientsRecipesWithId(1)).thenReturn(ingredientsRecipes);

        mockMvc.perform(delete("/api/ingredients/recipes/1"))
                .andExpect(status().isNoContent());

        verify(ingredientsRecipesService, times(1)).deleteIngredientsRecipes(1);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testDeleteIngredientsRecipesNotAdminOrOwner() throws Exception {
        IngredientsRecipes ingredientsRecipes = new IngredientsRecipes();
        Recipe recipe = new Recipe();
        User owner = new User();
        owner.setEmail("owner@example.com");
        recipe.setOwner(owner);
        ingredientsRecipes.setRecipe(recipe);

        when(ingredientsRecipesService.findIngredientsRecipesWithId(1)).thenReturn(ingredientsRecipes);

        mockMvc.perform(delete("/api/ingredients/recipes/1"))
                .andExpect(status().isForbidden());

        verify(ingredientsRecipesService, times(0)).deleteIngredientsRecipes(1);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testEditIngredientsRecipes() throws Exception {
        IngredientsRecipes ingredientsRecipes = new IngredientsRecipes();
        Recipe recipe = new Recipe();
        User owner = new User();
        owner.setEmail("user@example.com");
        recipe.setOwner(owner);
        ingredientsRecipes.setRecipe(recipe);
        ingredientsRecipes.setAmount(1.0);

        IngredientsRecipesDto ingredientsRecipesDto = new IngredientsRecipesDto();
        ingredientsRecipesDto.setAmount(2.0);

        when(ingredientsRecipesService.findIngredientsRecipesWithId(1)).thenReturn(ingredientsRecipes);

        mockMvc.perform(put("/api/ingredients/recipes/1")
                        .contentType("application/json")
                        .content("{\"amount\":2.0}"))
                .andExpect(status().isNoContent());

        assertEquals(2.0, ingredientsRecipes.getAmount());
        verify(ingredientsRecipesService, times(1)).editIngredientsRecipes(ingredientsRecipes);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testEditIngredientsRecipesNotOwner() throws Exception {
        IngredientsRecipes ingredientsRecipes = new IngredientsRecipes();
        Recipe recipe = new Recipe();
        User owner = new User();
        owner.setEmail("owner@example.com");
        recipe.setOwner(owner);
        ingredientsRecipes.setRecipe(recipe);
        ingredientsRecipes.setAmount(1.0);

        IngredientsRecipesDto ingredientsRecipesDto = new IngredientsRecipesDto();
        ingredientsRecipesDto.setAmount(2.0);

        when(ingredientsRecipesService.findIngredientsRecipesWithId(1)).thenReturn(ingredientsRecipes);

        mockMvc.perform(put("/api/ingredients/recipes/1")
                        .contentType("application/json")
                        .content("{\"amount\":2.0}"))
                .andExpect(status().isForbidden());

        verify(ingredientsRecipesService, times(0)).editIngredientsRecipes(any(IngredientsRecipes.class));
    }
}
