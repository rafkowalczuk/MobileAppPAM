package com.example.recipesapp.controller;

import com.example.recipesapp.dto.CommentDto;
import com.example.recipesapp.entity.Comment;
import com.example.recipesapp.entity.Recipe;
import com.example.recipesapp.entity.User;
import com.example.recipesapp.exceptions.NotAnAuthorException;
import com.example.recipesapp.service.CommentService;
import com.example.recipesapp.service.RecipeService;
import com.example.recipesapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;
    private final RecipeService recipeService;

    @GetMapping(value = "/my-comments")
    public ResponseEntity<List<Comment>> getAllRatingsForUser(@AuthenticationPrincipal UserDetails details) {
        User user = userService.findByEmail(details.getUsername());
        List<Comment> allCommentsWithUser = commentService.getCommentsWithUser(user);
        return new ResponseEntity<>(allCommentsWithUser, HttpStatus.OK);
    }

    @GetMapping(value = "/search/", params = "recipeId")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<List<Comment>> getRatingsForRecipe(@RequestParam Long recipeId) {
        Recipe recipeWithId = recipeService.getRecipe(recipeId);
        List<Comment> allCommentsForRecipe = commentService.getCommentsWithRecipe(recipeWithId);
        return new ResponseEntity<>(allCommentsForRecipe, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Comment>> getAllRatings() {
        List<Comment> allComments = commentService.getComments();
        return new ResponseEntity<>(allComments, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<Comment> postRating(@RequestBody CommentDto commentDto,
                                             @AuthenticationPrincipal UserDetails details) {
        User user = userService.findByEmail(details.getUsername());
        Recipe recipe = recipeService.getRecipe(commentDto.getRecipeId());

        Comment newComment = new Comment();
        newComment.setUser(user);
        newComment.setRecipe(recipe);
        newComment.setComment(commentDto.getComment());

        Comment comment = commentService.addComment(newComment);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @DeleteMapping(value = "{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public  ResponseEntity<Void> deleteComment(@PathVariable final Integer id, @AuthenticationPrincipal UserDetails details) {
        Comment commentToDelete = commentService.getCommentWithId(id);
        if (!commentToDelete.getRecipe().getOwner().getEmail().equals(details.getUsername()) && !details.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))){
            throw new NotAnAuthorException();
        } else {
            commentService.deleteComment(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PutMapping(value = "{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> editComment(@PathVariable final Integer id, @Valid @RequestBody CommentDto commentDto,
                                 @AuthenticationPrincipal UserDetails details) {
        Comment commentToEdit = commentService.getCommentWithId(id);

        if (!commentToEdit.getRecipe().getOwner().getEmail().equals(details.getUsername())){
            throw new NotAnAuthorException();
        } else {
            commentToEdit.setComment(commentDto.getComment());
            commentService.editComment(commentToEdit);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
