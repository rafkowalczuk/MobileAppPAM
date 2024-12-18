package com.example.recipesapp.repositories;

import com.example.recipesapp.entity.Comment;
import com.example.recipesapp.entity.Recipe;
import com.example.recipesapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository  extends JpaRepository<Comment, Integer> {
    List<Comment> findByUser(User user);
    List<Comment> findByRecipe(Recipe recipe);
}
