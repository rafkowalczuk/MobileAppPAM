package com.example.recipesapp.controller;

import com.example.recipesapp.entity.Tag;
import com.example.recipesapp.exceptions.NotAnAuthorException;
import com.example.recipesapp.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tag")
public class TagController {

    private final TagService tagService;

    @GetMapping("/all")
    public ResponseEntity<List<Tag>> getAllTags() {
        List<Tag> allTags = tagService.getTags();
        return new ResponseEntity<>(allTags, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<Tag> addTag(@RequestBody Tag tag) {
        Tag newTag = tagService.addTag(tag);
        return ResponseEntity.ok(newTag);
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<Tag> getUnit(@PathVariable final Long id) {
        Tag tag = tagService.getTagWithId(id);
        return new ResponseEntity<>(tag, HttpStatus.OK);
    }

    @DeleteMapping(value = "{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public  ResponseEntity<Void> deleteTag(@PathVariable final Long id, @AuthenticationPrincipal UserDetails details) {
        Tag tagToDelete = tagService.getTagWithId(id);
        if (!details.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))){
            throw new NotAnAuthorException();
        } else {
            tagService.deleteTag(tagToDelete);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
