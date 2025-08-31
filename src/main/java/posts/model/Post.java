// posts/model/Post.java
package posts.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "posts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=255) private String title;
    @Column(nullable=false)             private Integer price;
    @Column(name="seller_id", nullable=false) private Long sellerId;
    @Column(name="seller_name", nullable=false, length=255) private String sellerName;

    @Column(length=100) private String category;
    @Column(length=100) private String color;
    @Column(length=10)  private String gender;

    @Lob
    @Basic(fetch = FetchType.LAZY)           // don’t load bytes unless needed
    @Column(name = "image", columnDefinition = "bytea")
    private byte[] image;                    // actual image bytes

    @Column(name = "image_content_type", length = 100)
    private String imageContentType;         // e.g. "image/jpeg"

    @Column(name = "image_filename", length = 255)
    private String imageFilename;            // original filename

    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
