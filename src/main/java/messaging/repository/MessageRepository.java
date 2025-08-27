package messaging.repository;

import messaging.model.Message;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // inbox by receiver, sorted by your `timestamp` column
    Page<Message> findByReceiverIdOrderByCreatedAtDesc(Long receiverId, Pageable pageable);

    // optional: full conversation
    @Query("""
    select m from Message m
    where (m.senderId = :a and m.receiverId = :b)
       or (m.senderId = :b and m.receiverId = :a)
    order by m.createdAt asc
  """)
    Page<Message> findThread(@Param("a") Long a, @Param("b") Long b, Pageable pageable);
}
