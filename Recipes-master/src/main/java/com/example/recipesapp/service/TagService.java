package com.example.recipesapp.service;

import com.example.recipesapp.entity.Rating;
import com.example.recipesapp.entity.Recipe;
import com.example.recipesapp.entity.Tag;
import com.example.recipesapp.exceptions.TagBadRequestException;
import com.example.recipesapp.exceptions.TagNotFoundException;
import com.example.recipesapp.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public List<Tag> getTags() {
        return tagRepository.findAll();
    }

    public Tag getTagWithId(Long id) {
        return tagRepository.findById(id).orElseThrow(TagNotFoundException::new);
    }

    public Tag getTagWithName(String tag) {
        return tagRepository.findByTagIgnoreCase(tag).orElseThrow(TagNotFoundException::new);
    }

    public List<Tag> getTagWithRecipe(Recipe recipe) {
        System.out.println(tagRepository.findAllByRecipesContains(recipe));
        return tagRepository.findAllByRecipesContains(recipe);
    }

    public Tag addTag(Tag newTag) {
        try {
            return tagRepository.save(newTag);
        } catch (Exception exception) {
            throw new TagBadRequestException();
        }
    }

    @Transactional
    public void deleteTag(Tag tagToDelete) {
        try {
            tagRepository.delete(tagToDelete);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete tag with ID " + tagToDelete.getId(), e);
        }
    }

}
