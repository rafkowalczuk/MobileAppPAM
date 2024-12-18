package com.example.recipesapp;

import com.example.recipesapp.controller.RecipeController;
import com.example.recipesapp.entity.Recipe;
import com.example.recipesapp.entity.Tag;
import com.example.recipesapp.entity.User;
import com.example.recipesapp.service.RecipeService;
import com.example.recipesapp.service.TagService;
import com.example.recipesapp.service.UserService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeController.class)
@Import(TestSecurityConfig.class)
public class RecipeControllerTest {

    @MockBean
    private RecipeService recipeService;

    @MockBean
    private UserService userService;

    @MockBean
    private TagService tagService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testGetAllRecipes() throws Exception {
        when(recipeService.getRecipes()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/recipe/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testGetRecipe() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Pasta");

        when(recipeService.getRecipe(1L)).thenReturn(recipe);

        mockMvc.perform(get("/api/recipe/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pasta"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testPostRecipe() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Pasta");

        User user = new User();
        user.setEmail("user@example.com");

        when(userService.findByEmail("user@example.com")).thenReturn(user);
        when(recipeService.addRecipe(any(Recipe.class))).thenReturn(recipe);

        mockMvc.perform(post("/api/recipe/new")
                        .contentType("application/json")
                        .content("{\"name\":\"Pasta\",\"categories\":[\"Italian\"],\"description\":\"Delicious pasta\",\"steps\":[\"Boil water\",\"Cook pasta\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pasta"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testDeleteRecipe() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        User user = new User();
        user.setEmail("user@example.com");
        recipe.setOwner(user);

        when(recipeService.getRecipe(1L)).thenReturn(recipe);

        mockMvc.perform(delete("/api/recipe/1"))
                .andExpect(status().isNoContent());

        verify(recipeService, times(1)).deleteRecipe(1L);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testDeleteRecipeNotOwner() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        User owner = new User();
        owner.setEmail("owner@example.com");
        recipe.setOwner(owner);

        when(recipeService.getRecipe(1L)).thenReturn(recipe);

        mockMvc.perform(delete("/api/recipe/1"))
                .andExpect(status().isForbidden());

        verify(recipeService, times(0)).deleteRecipe(1L);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testEditRecipe() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Pasta");

        User user = new User();
        user.setEmail("user@example.com");
        recipe.setOwner(user);

        when(recipeService.getRecipe(1L)).thenReturn(recipe);

        mockMvc.perform(put("/api/recipe/1")
                        .contentType("application/json")
                        .content("{\"name\":\"Updated Pasta\",\"categories\":[\"Italian\"],\"description\":\"Delicious pasta\",\"steps\":[\"Boil water\",\"Cook pasta\"]}"))
                .andExpect(status().isNoContent());

        verify(recipeService, times(1)).editRecipe(anyLong(), any(Recipe.class));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testEditRecipeNotOwner() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        User owner = new User();
        owner.setEmail("owner@example.com");
        recipe.setOwner(owner);

        when(recipeService.getRecipe(1L)).thenReturn(recipe);

        mockMvc.perform(put("/api/recipe/1")
                        .contentType("application/json")
                        .content("{\"name\":\"Updated Pasta\",\"categories\":[\"Italian\"],\"description\":\"Delicious pasta\",\"steps\":[\"Boil water\",\"Cook pasta\"]}"))
                .andExpect(status().isForbidden());

        verify(recipeService, times(0)).editRecipe(anyLong(), any(Recipe.class));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testAddCategoryToRecipe() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        User user = new User();
        user.setEmail("user@example.com");
        recipe.setOwner(user);

        when(recipeService.getRecipe(1L)).thenReturn(recipe);
        when(recipeService.addCategoryToRecipe(1L, "Italian")).thenReturn(recipe);

        mockMvc.perform(post("/api/recipe/1/categories/add")
                        .contentType("application/json")
                        .content("\"Italian\""))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testAddDirectionToRecipe() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        User user = new User();
        user.setEmail("user@example.com");
        recipe.setOwner(user);

        when(recipeService.getRecipe(1L)).thenReturn(recipe);
        when(recipeService.addStepToRecipe(1L, "Boil water")).thenReturn(recipe);

        mockMvc.perform(post("/api/recipe/1/steps/add")
                        .contentType("application/json")
                        .content("\"Boil water\""))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testGetRecipesByCategory() throws Exception {
        when(recipeService.getRecipeWithCategory("Italian")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/recipe/search?category=Italian"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testGetRecipesByName() throws Exception {
        when(recipeService.getRecipeWithName("Pasta")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/recipe/search?name=Pasta"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testAddTagToRecipe() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setTag("Vegetarian");

        when(recipeService.addTagToRecipe(1L, 1L)).thenReturn(recipe);

        mockMvc.perform(post("/api/recipe/1/tags/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testGetTagsForRecipesWithId() throws Exception {
        when(recipeService.getTagsForRecipe(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/recipe/search/tag/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
//
//    public static List<Recipe> getAllRecipe() {
//        List<Recipe> allRecipe = new ArrayList<>();
//        allRecipe.add(getFirstRecipe());
//        allRecipe.add(getSecondRecipe());
//        return allRecipe;
//    }
}
