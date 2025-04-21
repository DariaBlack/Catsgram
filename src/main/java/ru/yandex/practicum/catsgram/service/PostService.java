package ru.yandex.practicum.catsgram.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.SortOrder;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

// Указываем, что класс PostService - является бином и его
// нужно добавить в контекст приложения
@Service
public class PostService {
    private final Map<Long, Post> posts = new HashMap<>();
    private final UserService userServices;

    @Autowired
    public PostService(UserService userServices) {
        this.userServices = userServices;
    }

    public Collection<Post> findAll(int from, int size, SortOrder sortOrder) {
        return posts.values().stream()
                .sorted(sortOrder.equals(sortOrder.ASCENDING)
                ? Comparator.comparing(Post::getPostDate)
                : Comparator.comparing(Post::getPostDate).reversed())
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }

    public Post create(Post post) {
        Long authorId = post.getAuthorId();

        Optional<User> optionalUser = userServices.findUserById(authorId);

        if (optionalUser.isEmpty()) {
            throw new ConditionsNotMetException("Автор с id = " + authorId + " не найден");
        }

        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    public Optional<Post> findPostById(long postId) {
        if (posts.containsKey(postId)) {
            return Optional.of(posts.get(postId));
        } else {
            return Optional.empty();
        }
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
