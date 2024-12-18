package com.example.recipesapp.controller;

import com.example.recipesapp.dto.FavouriteDto;
import com.example.recipesapp.entity.Favourite;
import com.example.recipesapp.entity.Recipe;
import com.example.recipesapp.entity.User;
import com.example.recipesapp.exceptions.NotAnAuthorException;
import com.example.recipesapp.service.FavouriteService;
import com.example.recipesapp.service.RecipeService;
import com.example.recipesapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/favourite")
public class FavouriteController {

    private final FavouriteService favouriteService;
    private final UserService userService;
    private final RecipeService recipeService;

    @GetMapping("/all")
    public ResponseEntity<List<Favourite>> getAllFavourites() {
        List<Favourite> allFavourites = favouriteService.getFavourite();
        return new ResponseEntity<>(allFavourites, HttpStatus.OK);
    }

    @GetMapping(value = "/my-favourites")
    public ResponseEntity<List<Favourite>> getAllFavouritesForUser(@AuthenticationPrincipal UserDetails details) {
        User user = userService.findByEmail(details.getUsername());
        List<Favourite> allRecipesWithUser = favouriteService.getFavouritesWithUser(user);
        return new ResponseEntity<>(allRecipesWithUser, HttpStatus.OK);
    }

    @GetMapping(value = "/search/", params = "recipeId")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<List<Favourite>> getFavouritesForRecipe(@RequestParam Long recipeId) {
        Recipe recipeWithId = recipeService.getRecipe(recipeId);
        List<Favourite> allFavouritesForRecipe = favouriteService.getFavouritesWithRecipe(recipeWithId);
        return new ResponseEntity<>(allFavouritesForRecipe, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<Favourite> postFavourite(@RequestBody FavouriteDto favouriteDto,
                                             @AuthenticationPrincipal UserDetails details) {
        User user = userService.findByEmail(details.getUsername());
        Recipe recipe = recipeService.getRecipe(favouriteDto.getRecipeId());

        Favourite newFavourite = new Favourite();
        newFavourite.setUser(user);
        newFavourite.setRecipe(recipe);

        Favourite favourite = favouriteService.addFavourite(newFavourite);
        return new ResponseEntity<>(favourite, HttpStatus.OK);
    }

    @DeleteMapping(value = "{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public  ResponseEntity<Void> deleteFavourite(@PathVariable final Integer id, @AuthenticationPrincipal UserDetails details) {
        Favourite favouriteToDelete = favouriteService.getFavouriteWithId(id);
        if (!favouriteToDelete.getUser().getEmail().equals(details.getUsername())){
            throw new NotAnAuthorException();
        } else {
            favouriteService.deleteRating(favouriteToDelete);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

}
