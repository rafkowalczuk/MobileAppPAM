package com.example.recipesapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({TagView.PostPut.class, TagView.Get.class})
    private long id;

    @NotBlank
    @Size(max = 50)
    @JsonView({TagView.PostPut.class, TagView.Get.class})
    @Column(unique = true)
    private String tag;

    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private List<Recipe> recipes = new ArrayList<>();
}
