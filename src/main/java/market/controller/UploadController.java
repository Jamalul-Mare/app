package market.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import market.model.ImagePost;
import market.service.ImagePostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/image-posts")
@RequiredArgsConstructor
public class UploadController {

    private final ImagePostService service;

    // --- READ: listare toate ---
    @GetMapping
    public ResponseEntity<List<ImagePost>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // --- READ: by id ---
    @GetMapping("/{id}")
    public ResponseEntity<ImagePost> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- CREATE ---
    @PostMapping
    public ResponseEntity<ImagePost> create(@AuthenticationPrincipal userauth.user.User authUser,
                                            @Valid @RequestBody CreateImagePostDto dto,
                                            UriComponentsBuilder uriBuilder) {
        // Dacă NU ai auth, comentează linia de mai jos și folosește dto.getUserId() ca să încarci userul din DB.
        if (authUser == null) return ResponseEntity.status(401).build();

        ImagePost post = new ImagePost();
        post.setImageUrl(dto.getImageUrl());
        post.setDescription(dto.getDescription());
        post.setPrice(dto.getPrice().doubleValue());
        post.setUser(authUser);

        ImagePost saved = service.save(post);

        URI location = uriBuilder.path("/api/image-posts/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    // --- UPDATE (full sau partial pe câmpurile permise) ---
    @PutMapping("/{id}")
    public ResponseEntity<ImagePost> update(@AuthenticationPrincipal userauth.user.User authUser,
                                            @PathVariable Long id,
                                            @Valid @RequestBody UpdateImagePostDto dto) {
        return service.getById(id).map(existing -> {
            // opțional: verifică proprietatea resursei
            // if (!existing.getUser().getId().equals(authUser.getId())) return ResponseEntity.status(403).build();

            existing.setImageUrl(dto.getImageUrl() != null ? dto.getImageUrl() : existing.getImageUrl());
            existing.setDescription(dto.getDescription() != null ? dto.getDescription() : existing.getDescription());
            if (dto.getPrice() != null) existing.setPrice(dto.getPrice().doubleValue());

            ImagePost saved = service.save(existing);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    // --- DELETE ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal userauth.user.User authUser,
                                       @PathVariable Long id) {
        if (authUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).<Void>build();

        var opt = service.getById(id);
        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).<Void>build();

        var existing = opt.get();
        // optional ownership check here
        service.delete(existing.getId());
        return ResponseEntity.noContent().build();
    }



    // ===================== DTO-uri =====================

    @Data
    public static class CreateImagePostDto {
        @NotBlank
        @Size(max = 2048)
        private String imageUrl;

        @Size(max = 1000)
        private String description;

        @NotNull
        @DecimalMin(value = "0.0", inclusive = true, message = "Prețul trebuie să fie >= 0")
        private BigDecimal price;

        // Dacă NU folosești autentificare, poți adăuga:
        // @NotNull private Long userId;
    }

    @Data
    public static class UpdateImagePostDto {
        @Size(max = 2048)
        private String imageUrl;

        @Size(max = 1000)
        private String description;

        @DecimalMin(value = "0.0", inclusive = true, message = "Prețul trebuie să fie >= 0")
        private BigDecimal price;
    }
}
