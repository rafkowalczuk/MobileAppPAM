package com.example.recipesapp;

import com.example.recipesapp.controller.FavouriteController;
import com.example.recipesapp.dto.FavouriteDto;
import com.example.recipesapp.entity.Favourite;
import com.example.recipesapp.entity.Recipe;
import com.example.recipesapp.entity.User;
import com.example.recipesapp.service.FavouriteService;
import com.example.recipesapp.service.RecipeService;
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

@WebMvcTest(FavouriteController.class)
@Import(TestSecurityConfig.class)
public class FavouriteControllerTest {

    @MockBean
    private FavouriteService favouriteService;

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
    @WithMockUser(username = "test@example.com", roles = "USER")
    public void testGetAllFavourites() throws Exception {
        when(favouriteService.getFavourite()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/favourite/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    public void testGetAllFavouritesForUser() throws Exception {
        User user = new User();
        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(favouriteService.getFavouritesWithUser(user)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/favourite/my-favourites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    public void testPostFavourite() throws Exception {
        User user = new User();
        Recipe recipe = new Recipe();
        FavouriteDto favouriteDto = new FavouriteDto();
        favouriteDto.setRecipeId(1L);

        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(recipeService.getRecipe(1L)).thenReturn(recipe);
        when(favouriteService.addFavourite(any(Favourite.class))).thenReturn(new Favourite());

        mockMvc.perform(post("/api/favourite/new")
                        .contentType("application/json")
                        .content("{\"recipeId\":1}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    public void testDeleteFavourite() throws Exception {
        Favourite favourite = new Favourite();
        User user = new User();
        user.setEmail("test@example.com");
        favourite.setUser(user);

        when(favouriteService.getFavouriteWithId(1)).thenReturn(favourite);

        mockMvc.perform(delete("/api/favourite/1"))
                .andExpect(status().isNoContent());

        verify(favouriteService, times(1)).deleteRating(favourite);
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    public void testDeleteFavouriteNotAuthor() throws Exception {
        Favourite favourite = new Favourite();
        User owner = new User();
        owner.setEmail("author@example.com");
        favourite.setUser(owner);

        when(favouriteService.getFavouriteWithId(1)).thenReturn(favourite);

        mockMvc.perform(delete("/api/favourite/1"))
                .andExpect(status().isForbidden());

        verify(favouriteService, times(0)).deleteRating(favourite);
    }
}
