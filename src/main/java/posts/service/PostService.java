// posts/service/PostService.java
package posts.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import posts.model.Post;
import posts.repository.PostRepository;

@Service @RequiredArgsConstructor
public class PostService {
    private final PostRepository repo;

    public Page<Post> list(int page, int size, Sort sort) {
        return repo.findAll(PageRequest.of(page, size, sort));
    }

    public Post create(Post p) { return repo.save(p); }

    public Post get(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }

    public Post save(Post p) { return repo.save(p); }
}
