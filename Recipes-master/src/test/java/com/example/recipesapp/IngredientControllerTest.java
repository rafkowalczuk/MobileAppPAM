package com.example.recipesapp;




import com.example.recipesapp.controller.IngredientController;
import com.example.recipesapp.entity.Ingredient;
import com.example.recipesapp.service.IngredientsService;
import com.example.recipesapp.service.RecipeService;
import com.example.recipesapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = IngredientController.class)
@Import(TestSecurityConfig.class)
public class IngredientControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IngredientsService ingredientsService;

    @MockBean
    private UserService userService;

    @MockBean
    private RecipeService recipeService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void testGetAllIngredients() throws Exception {
        Ingredient ingredient = new Ingredient(1, "Tomato");
        Mockito.when(ingredientsService.findAllIngredients()).thenReturn(Collections.singletonList(ingredient));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/ingredient/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ingredient").value("Tomato"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void testAddIngredient() throws Exception {
        Ingredient ingredient = new Ingredient(1, "Tomato");
        Mockito.when(ingredientsService.saveIngredient(any(Ingredient.class))).thenReturn(ingredient);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/ingredient/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ingredient\": \"Tomato\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingredient").value("Tomato"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void testGetIngredient() throws Exception {
        Ingredient ingredient = new Ingredient(1, "Tomato");
        Mockito.when(ingredientsService.getIngredientWithId(anyInt())).thenReturn(ingredient);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/ingredient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingredient").value("Tomato"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testDeleteIngredientAsAdmin() throws Exception {
        Ingredient ingredient = new Ingredient(1, "Tomato");
        Mockito.when(ingredientsService.getIngredientWithId(anyInt())).thenReturn(ingredient);
        Mockito.doNothing().when(ingredientsService).deleteIngredient(any(Ingredient.class));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/ingredient/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void testDeleteIngredientAsUser() throws Exception {
        Ingredient ingredient = new Ingredient(1, "Tomato");
        Mockito.when(ingredientsService.getIngredientWithId(anyInt())).thenReturn(ingredient);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/ingredient/1"))
                .andExpect(status().isForbidden());
    }
}
