package posts.api;

import jakarta.validation.constraints.*;
import posts.model.Gender;

public record PostRequest(
        @NotBlank @Size(max=255) String title,
        @NotNull @Positive Integer price,
        @NotBlank @Size(max=2000) String image,
        @NotNull Long sellerId,
        @NotBlank @Size(max=255) String sellerName,
        @Size(max=100) String category,
        @Size(max=100) String color,
        Gender gender
) {}
