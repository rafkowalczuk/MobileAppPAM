package com.example.recipesapp.service;

import com.example.recipesapp.entity.Favourite;
import com.example.recipesapp.entity.Recipe;
import com.example.recipesapp.entity.User;
import com.example.recipesapp.exceptions.CommentBadRequestException;
import com.example.recipesapp.exceptions.FavouriteBadRequestException;
import com.example.recipesapp.exceptions.FavouriteNotFoundException;
import com.example.recipesapp.repositories.FavouriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavouriteService {
    private final FavouriteRepository favouriteRepository;

    public List<Favourite> getFavouritesWithUser(User user) {
        return favouriteRepository.findByUser(user);
    }

    public List<Favourite> getFavourite() {
        return favouriteRepository.findAll();
    }

    public List<Favourite> getFavouritesWithRecipe(Recipe recipe) {
        return favouriteRepository.findByRecipe(recipe);
    }

    public Favourite addFavourite(Favourite newFavourite) {
        try {
            newFavourite.setDate(LocalDateTime.now());
            return favouriteRepository.save(newFavourite);
        } catch (Exception exception) {
            throw new FavouriteBadRequestException();
        }
    }

    public Favourite getFavouriteWithId(Integer id) {
        return favouriteRepository.findById(id).orElseThrow(FavouriteNotFoundException::new);
    }

    @Transactional
    public void deleteRating(Favourite favouriteToDelete) {
        try {
            favouriteRepository.delete(favouriteToDelete);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete comment with ID " + favouriteToDelete.getId(), e);
        }
    }


}
