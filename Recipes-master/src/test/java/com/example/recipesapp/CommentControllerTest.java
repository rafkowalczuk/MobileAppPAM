package com.example.recipesapp;

import com.example.recipesapp.controller.CommentController;
import com.example.recipesapp.dto.CommentDto;
import com.example.recipesapp.entity.Comment;
import com.example.recipesapp.entity.Recipe;
import com.example.recipesapp.entity.User;
import com.example.recipesapp.service.CommentService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CommentController.class)
@Import(TestSecurityConfig.class)
public class CommentControllerTest {
    @MockBean
    private CommentService commentService;

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
    public void testGetAllRatingsForUser() throws Exception {
        User user = new User();
        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(commentService.getCommentsWithUser(user)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/comment/my-comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    public void testGetAllRatings() throws Exception {
        when(commentService.getComments()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/comment/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    public void testPostRating() throws Exception {
        User user = new User();
        Recipe recipe = new Recipe();
        CommentDto commentDto = new CommentDto();
        commentDto.setRecipeId(1L);
        commentDto.setComment("Great recipe!");

        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(recipeService.getRecipe(1L)).thenReturn(recipe);
        when(commentService.addComment(any(Comment.class))).thenReturn(new Comment());

        mockMvc.perform(post("/api/comment/new")
                        .contentType("application/json")
                        .content("{\"recipeId\":1,\"comment\":\"Great recipe!\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    public void testDeleteComment() throws Exception {
        Comment comment = new Comment();
        Recipe recipe = new Recipe();
        User owner = new User();
        owner.setEmail("test@example.com");
        recipe.setOwner(owner);
        comment.setRecipe(recipe);

        when(commentService.getCommentWithId(1)).thenReturn(comment);

        mockMvc.perform(delete("/api/comment/1"))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(1);
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    public void testDeleteCommentNotAuthor() throws Exception {
        Comment comment = new Comment();
        Recipe recipe = new Recipe();
        User owner = new User();
        owner.setEmail("author@example.com");
        recipe.setOwner(owner);
        comment.setRecipe(recipe);

        when(commentService.getCommentWithId(1)).thenReturn(comment);

        mockMvc.perform(delete("/api/comment/1"))
                .andExpect(status().isForbidden());

        verify(commentService, times(0)).deleteComment(1);
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    public void testEditComment() throws Exception {
        Comment comment = new Comment();
        Recipe recipe = new Recipe();
        User owner = new User();
        owner.setEmail("test@example.com");
        recipe.setOwner(owner);
        comment.setRecipe(recipe);
        comment.setComment("Old comment");

        CommentDto commentDto = new CommentDto();
        commentDto.setComment("New comment");

        when(commentService.getCommentWithId(1)).thenReturn(comment);

        mockMvc.perform(put("/api/comment/1")
                        .contentType("application/json")
                        .content("{\"comment\":\"New comment\"}"))
                .andExpect(status().isNoContent());

        assertEquals("New comment", comment.getComment());
        verify(commentService, times(1)).editComment(comment);
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    public void testEditCommentNotAuthor() throws Exception {
        Comment comment = new Comment();
        Recipe recipe = new Recipe();
        User owner = new User();
        owner.setEmail("author@example.com");
        recipe.setOwner(owner);
        comment.setRecipe(recipe);

        CommentDto commentDto = new CommentDto();
        commentDto.setComment("New comment");

        when(commentService.getCommentWithId(1)).thenReturn(comment);

        mockMvc.perform(put("/api/comment/1")
                        .contentType("application/json")
                        .content("{\"comment\":\"New comment\"}"))
                .andExpect(status().isForbidden());

        verify(commentService, times(0)).editComment(any(Comment.class));
    }
}
