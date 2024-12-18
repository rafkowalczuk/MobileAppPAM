package com.example.recipesapp.repositories;

import com.example.recipesapp.entity.Favourite;
import com.example.recipesapp.entity.Recipe;
import com.example.recipesapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Integer> {

    List<Favourite> findByUser(User user);
    List<Favourite> findByRecipe(Recipe recipe);
}
