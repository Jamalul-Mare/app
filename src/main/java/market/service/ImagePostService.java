package market.service;

import market.model.ImagePost;
import market.repository.ImagePostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImagePostService {
    private final ImagePostRepository repo;

    public ImagePostService(ImagePostRepository repo) {
        this.repo = repo;
    }

    public ImagePost save(ImagePost post) { return repo.save(post); }

    public List<ImagePost> getAll() { return repo.findAll(); }

    public Optional<ImagePost> getById(Long id) { return repo.findById(id); }

    public void delete(Long id) { repo.deleteById(id); }  // <— ADD THIS
}
