package com.example.recipesapp.service;

import com.example.recipesapp.entity.Comment;
import com.example.recipesapp.entity.Rating;
import com.example.recipesapp.entity.Recipe;
import com.example.recipesapp.entity.User;
import com.example.recipesapp.exceptions.CommentBadRequestException;
import com.example.recipesapp.exceptions.CommentNotFoundException;
import com.example.recipesapp.exceptions.RatingBadRequestException;
import com.example.recipesapp.exceptions.RatingNotFoundException;
import com.example.recipesapp.repositories.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;

    public List<Rating> getRatings() {
        return ratingRepository.findAll();
    }

    public List<Rating> getRatingsWithUser(User user) {
        return ratingRepository.findByUser(user);
    }
    public Rating getRatingWithId(Integer id) {
        return ratingRepository.findById(id).orElseThrow(RatingNotFoundException::new);
    }

    public List<Rating> getRatingsWithRecipe(Recipe recipe) {
        return ratingRepository.findByRecipe(recipe);
    }
    public Rating addRating(Rating newRating) {
        try {
            newRating.setDate(LocalDateTime.now());
            return ratingRepository.save(newRating);
        } catch (Exception exception) {
            throw new RatingBadRequestException();
        }
    }

    public Rating editRating(@Valid Rating newRating) {
        try {
            ratingRepository.save(newRating);
            return newRating;
        } catch (RuntimeException exception) {
            throw new CommentBadRequestException();
        }
    }

    @Transactional
    public void deleteRating(Rating ratingToDelete) {
        try {
            ratingRepository.delete(ratingToDelete);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete comment with ID " + ratingToDelete.getId(), e);
        }
    }


}
