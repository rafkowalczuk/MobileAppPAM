package com.example.recipesapp;


import com.example.recipesapp.controller.RatingController;
import com.example.recipesapp.dto.RatingDto;
import com.example.recipesapp.entity.Rating;
import com.example.recipesapp.entity.Recipe;
import com.example.recipesapp.entity.User;
import com.example.recipesapp.exceptions.NotAnAuthorException;
import com.example.recipesapp.service.RatingService;
import com.example.recipesapp.service.RecipeService;
import com.example.recipesapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RatingController.class)
@Import(TestSecurityConfig.class)
public class RatingControllerTest {


    @MockBean
    private RatingService ratingService;

    @MockBean
    private UserService userService;

    @MockBean
    private RecipeService recipeService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testGetAllRatingsForUser() throws Exception {
        User user = new User();
        when(userService.findByEmail("user@example.com")).thenReturn(user);
        when(ratingService.getRatingsWithUser(user)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/rating/my-ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testGetAllRatings() throws Exception {
        when(ratingService.getRatings()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/rating/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testPostRating() throws Exception {
        User user = new User();
        Recipe recipe = new Recipe();
        RatingDto ratingDto = new RatingDto();
        ratingDto.setRecipeId(1L);
        ratingDto.setRating(5);

        when(userService.findByEmail("user@example.com")).thenReturn(user);
        when(recipeService.getRecipe(1L)).thenReturn(recipe);
        when(ratingService.addRating(any(Rating.class))).thenReturn(new Rating());

        mockMvc.perform(post("/api/rating/new")
                        .contentType("application/json")
                        .content("{\"recipeId\":1,\"rating\":5}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    public void testDeleteRating() throws Exception {
        Rating rating = new Rating();
        User user = new User();
        user.setEmail("admin@example.com");
        rating.setUser(user);

        when(ratingService.getRatingWithId(1)).thenReturn(rating);

        mockMvc.perform(delete("/api/rating/1"))
                .andExpect(status().isNoContent());

        verify(ratingService, times(1)).deleteRating(rating);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testDeleteRatingNotAdminOrOwner() throws Exception {
        Rating rating = new Rating();
        User owner = new User();
        owner.setEmail("owner@example.com");
        rating.setUser(owner);

        when(ratingService.getRatingWithId(1)).thenReturn(rating);

        mockMvc.perform(delete("/api/rating/1"))
                .andExpect(status().isForbidden());

        verify(ratingService, times(0)).deleteRating(rating);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testEditRating() throws Exception {
        Rating rating = new Rating();
        User owner = new User();
        owner.setEmail("user@example.com");
        rating.setUser(owner);
        rating.setRating(3);

        RatingDto ratingDto = new RatingDto();
        ratingDto.setRating(5);

        when(ratingService.getRatingWithId(1)).thenReturn(rating);

        mockMvc.perform(put("/api/rating/1")
                        .contentType("application/json")
                        .content("{\"rating\":5}"))
                .andExpect(status().isNoContent());

        assertEquals(5, rating.getRating());
        verify(ratingService, times(1)).editRating(rating);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    public void testEditRatingNotOwner() throws Exception {
        Rating rating = new Rating();
        User owner = new User();
        owner.setEmail("owner@example.com");
        rating.setUser(owner);
        rating.setRating(3);

        RatingDto ratingDto = new RatingDto();
        ratingDto.setRating(5);

        when(ratingService.getRatingWithId(1)).thenReturn(rating);

        mockMvc.perform(put("/api/rating/1")
                        .contentType("application/json")
                        .content("{\"rating\":5}"))
                .andExpect(status().isForbidden());

        verify(ratingService, times(0)).editRating(any(Rating.class));
    }
}
