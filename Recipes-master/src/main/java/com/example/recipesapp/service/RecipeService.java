    package com.example.recipesapp.service;

    import com.example.recipesapp.entity.*;
    import com.example.recipesapp.exceptions.RecipeBadRequestException;
    import com.example.recipesapp.exceptions.RecipeNotFoundException;
    import com.example.recipesapp.exceptions.TagNotFoundException;
    import com.example.recipesapp.repositories.RecipesRepository;
    import com.example.recipesapp.repositories.TagRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;

    import javax.transaction.Transactional;
    import javax.validation.Valid;
    import java.time.LocalDateTime;
    import java.util.Comparator;
    import java.util.List;
    import java.util.Optional;

    @Service
    @RequiredArgsConstructor
    public class RecipeService {
        private final RecipesRepository recipesRepository;
        private final TagRepository tagRepository;
        private final CommentService commentService;
        private final FavouriteService favouriteService;
        private final IngredientsRecipesService ingredientsRecipesService;
        private final RatingService ratingService;

        public List<Recipe> getRecipes() {
            List<Recipe> allRecipes = recipesRepository.findAll();
            allRecipes.sort(Comparator.comparing(Recipe::getDate).reversed());
            return allRecipes;
        }

        public List<Recipe> getRecipeWithCategory(String category) {
            List<Recipe> recipesWithCategory = recipesRepository.findByCategoriesIgnoreCase(category);
            recipesWithCategory.sort(Comparator.comparing(Recipe::getDate).reversed());
            return recipesWithCategory;
        }

        public List<Recipe> getRecipeWithName(String name) {
            List<Recipe> recipesWithName = recipesRepository.findByNameContainsIgnoreCase(name);
            recipesWithName.sort(Comparator.comparing(Recipe::getDate).reversed());
            return recipesWithName;
        }

        public Recipe getRecipe(Long id) {
            return recipesRepository.findById(id).orElseThrow(RecipeNotFoundException::new);
        }

        public Recipe addRecipe(Recipe newRecipe) {
            try {
                newRecipe.setDate(LocalDateTime.now());
                return recipesRepository.save(newRecipe);
            } catch (Exception exception) {
                throw new RecipeBadRequestException();
            }
        }

        public void deleteRecipe(Long id) {
            Recipe recipe = getRecipe(id);
            List<Comment> allRecipesComments = commentService.getCommentsWithRecipe(recipe);
            List<Favourite> allRecipesFavourite = favouriteService.getFavouritesWithRecipe(recipe);
            List<IngredientsRecipes> allRecipesIngredientsRecipes = ingredientsRecipesService.findAllIngredientsRecipesWithRecipe(id);
            List<Rating> allRecipesRating = ratingService.getRatingsWithRecipe(recipe);

            allRecipesComments.forEach(comment -> commentService.deleteComment(comment.getId()));
            allRecipesFavourite.forEach(favouriteService::deleteRating);
            allRecipesIngredientsRecipes.forEach(ingredientsRecipes -> ingredientsRecipesService.deleteIngredientsRecipes(ingredientsRecipes.getId()));
            allRecipesRating.forEach(ratingService::deleteRating);

            recipesRepository.delete(recipe);
        }

        public Recipe editRecipe(Long id, @Valid Recipe recipe) {
            Recipe recipeToEdit = getRecipe(id);
            try {
                recipeToEdit.setDescription(recipe.getDescription());
                recipeToEdit.setSteps(recipe.getSteps());
                recipeToEdit.setName(recipe.getName());
                recipeToEdit.setCategories(recipe.getCategories());
                recipeToEdit.setDate(LocalDateTime.now());
                recipesRepository.save(recipeToEdit);
                return recipeToEdit;
            } catch (RuntimeException exception) {
                throw new RecipeBadRequestException();
            }
        }

        @Transactional
        public Recipe addStepToRecipe(Long recipeId, String step) {
            Recipe recipe = getRecipe(recipeId);
            recipe.setDate(LocalDateTime.now());
            recipe.addStep(step);
            return recipe;
        }

        @Transactional
        public Recipe addCategoryToRecipe(Long recipeId, String category) {
            Recipe recipe = getRecipe(recipeId);
            recipe.setDate(LocalDateTime.now());
            recipe.addCategory(category);
            return recipe;
        }


        @Transactional
        public Recipe addTagToRecipe(Long recipeId, Long tagId) {
            Optional<Recipe> optionalRecipe = recipesRepository.findById(recipeId);
            Optional<Tag> optionalTag = tagRepository.findById(tagId);

            if (optionalRecipe.isPresent() && optionalTag.isPresent()) {
                Recipe recipe = optionalRecipe.get();
                Tag tag = optionalTag.get();
                recipe.getTags().add(tag);
                Recipe result = recipesRepository.save(recipe);
                return result;
            } else {
                if (optionalRecipe.isEmpty()) {
                    throw new RecipeNotFoundException();
                } else {
                    throw new TagNotFoundException();
                }
            }
        }

        public List<Tag> getTagsForRecipe(Long recipeId) {
            return recipesRepository.findTagsByRecipeId(recipeId);
        }

    }
