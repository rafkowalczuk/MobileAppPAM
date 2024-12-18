package com.example.recipesapp.repositories;

import com.example.recipesapp.entity.Recipe;
import com.example.recipesapp.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByTagIgnoreCase(String tagName);
    List<Tag> findAllByRecipesContains(Recipe recipe);

}
