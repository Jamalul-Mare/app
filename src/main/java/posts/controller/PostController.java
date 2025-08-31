// posts/controller/PostController.java
package posts.controller;

import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import posts.model.Post;
import posts.api.PostResponse;
import posts.service.PostService;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/api1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService service;

    private PostResponse toDto(Post p) {
        boolean hasImage = p.getImage() != null && p.getImage().length > 0;
        String imageUrl = hasImage ? ("/api1/posts/" + p.getId() + "/image") : null;
        return new PostResponse(
                p.getId(),
                p.getTitle(),
                p.getPrice(),
                String.valueOf(p.getSellerId()),
                p.getSellerName(),
                p.getCategory(),
                p.getColor(),
                p.getGender(),
                p.getCreatedAt().toString(),
                hasImage,
                imageUrl
        );
    }

    // List (simple version; add your filters if needed)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<PostResponse> list(@RequestParam(defaultValue="0") int page,
                                   @RequestParam(defaultValue="20") int size,
                                   @RequestParam(defaultValue="createdAt,desc") String sort) {
        Sort s = sort.endsWith(",asc")
                ? Sort.by(sort.split(",")[0]).ascending()
                : Sort.by(sort.split(",")[0]).descending();
        return service.list(page, size, s).map(this::toDto);
    }

    // Create post with image (multipart/form-data)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PostResponse create(
            @RequestParam String title,
            @RequestParam Integer price,
            @RequestParam Long sellerId,
            @RequestParam String sellerName,
            @RequestParam(required=false) String category,
            @RequestParam(required=false) String color,
            @RequestParam(required=false) String gender,
            @RequestParam(required=false) MultipartFile image
    ) throws IOException {

        Post p = new Post();
        p.setTitle(title.trim());
        p.setPrice(price);
        p.setSellerId(sellerId);
        p.setSellerName(sellerName.trim());
        p.setCategory(category);
        p.setColor(color);
        p.setGender(gender);

        if (image != null && !image.isEmpty()) {
            p.setImage(image.getBytes());
            p.setImageContentType(Objects.toString(image.getContentType(), "application/octet-stream"));
            p.setImageFilename(image.getOriginalFilename());
        }

        Post saved = service.create(p);
        return toDto(saved);
    }

    // Replace image for an existing post
    @PutMapping(path="/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PostResponse uploadImage(@PathVariable Long id,
                                    @RequestPart("image") MultipartFile image) throws IOException {
        Post p = service.get(id);
        p.setImage(image.getBytes());
        p.setImageContentType(Objects.toString(image.getContentType(), "application/octet-stream"));
        p.setImageFilename(image.getOriginalFilename());
        return toDto(service.save(p));
    }

    // Serve the image bytes
    @GetMapping(path="/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Post p = service.get(id);
        if (p.getImage() == null || p.getImage().length == 0) {
            return ResponseEntity.notFound().build();
        }
        MediaType mt = MediaType.APPLICATION_OCTET_STREAM;
        try {
            if (p.getImageContentType() != null) mt = MediaType.parseMediaType(p.getImageContentType());
        } catch (Exception ignored) {}
        return ResponseEntity.ok()
                .contentType(mt)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + (p.getImageFilename() != null ? p.getImageFilename() : ("post-" + id)) + "\"")
                .body(p.getImage());
    }
}
