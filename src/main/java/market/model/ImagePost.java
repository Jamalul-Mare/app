package market.model;

import jakarta.persistence.*;
import lombok.*;
import userauth.user.User;

@Entity
@Table(name = "image_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;
    private String description;
    private Double price;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
