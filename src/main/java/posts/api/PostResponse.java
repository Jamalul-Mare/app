// posts/api/PostResponse.java
package posts.api;

public record PostResponse(
        Long id,
        String title,
        Integer price,
        String sellerId,
        String sellerName,
        String category,
        String color,
        String gender,
        String createdAt,
        boolean hasImage,
        String imageUrl           // e.g. /api1/posts/{id}/image
) {}
