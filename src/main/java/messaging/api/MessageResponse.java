package messaging.api;

public record MessageResponse(
        Long id, Long senderId, Long receiverId, String content, String createdAt, boolean isRead
) {}
