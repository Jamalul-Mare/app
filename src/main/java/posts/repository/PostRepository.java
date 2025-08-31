package posts.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import posts.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findBySellerId(Long sellerId, Pageable pageable);

    @Query("""
      select p from Post p
      where (:sellerId is null or p.sellerId = :sellerId)
        and (:excludeSellerId is null or p.sellerId <> :excludeSellerId)
        and (:category is null or lower(p.category) = lower(:category))
        and (:color is null or lower(p.color) = lower(:color))
        and (:gender is null or str(p.gender) = :gender)
        and (:q is null or lower(p.title) like lower(concat('%', :q, '%')))
      """)
    Page<Post> search(
            @Param("sellerId") Long sellerId,
            @Param("excludeSellerId") Long excludeSellerId,
            @Param("category") String category,
            @Param("color") String color,
            @Param("gender") String gender,   // pass enum name or null
            @Param("q") String q,
            Pageable pageable
    );
}
