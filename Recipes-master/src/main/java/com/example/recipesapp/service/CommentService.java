package com.example.recipesapp.service;

import com.example.recipesapp.entity.Comment;
import com.example.recipesapp.entity.Recipe;
import com.example.recipesapp.entity.User;
import com.example.recipesapp.exceptions.CommentBadRequestException;
import com.example.recipesapp.exceptions.CommentNotFoundException;
import com.example.recipesapp.exceptions.RecipeBadRequestException;
import com.example.recipesapp.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public List<Comment> getCommentsWithUser(User user) {
        return commentRepository.findByUser(user);
    }

    public List<Comment> getComments() {
        return commentRepository.findAll();
    }

    public List<Comment> getCommentsWithRecipe(Recipe recipe) {
        return commentRepository.findByRecipe(recipe);
    }

    public Comment addComment(Comment newComment) {
        try {
            newComment.setDate(LocalDateTime.now());
            return commentRepository.save(newComment);
        } catch (Exception exception) {
            throw new CommentBadRequestException();
        }
    }

    public Comment getCommentWithId(Integer id) {
        return commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);
    }

    @Transactional
    public void deleteComment(Integer id) {
        try {
            commentRepository.delete(getCommentWithId(id));
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete comment with ID " + id, e);
        }
    }

    public Comment editComment(@Valid Comment newComment) {
        try {
            commentRepository.save(newComment);
            return newComment;
        } catch (RuntimeException exception) {
            throw new CommentBadRequestException();
        }
    }

}
