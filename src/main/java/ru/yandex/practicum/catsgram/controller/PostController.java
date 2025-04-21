package ru.yandex.practicum.catsgram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.SortOrder;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Collection<Post> findAll(@RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(defaultValue =  "desc") String sort) {
        SortOrder sortOrder = SortOrder.from(sort);

        if (sortOrder == null) {
            throw new IllegalArgumentException("Значение параметра sort должно быть asc или desc");
        }

        if (size <= 0) {
            throw new IllegalArgumentException("Параметр size должен быть больше 0");
        }
        return postService.findAll(from, size, sortOrder);
    }

    @GetMapping("/posts/{postId}")
    public Optional<Post> findPostById(@PathVariable long postId) {
        return postService.findPostById(postId);
    }

    @PostMapping
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }
}
