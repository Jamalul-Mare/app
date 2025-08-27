package messaging.api;

import jakarta.validation.constraints.*;

public record SendMessageRequest(
        @NotNull Long senderId,
        @NotNull Long receiverId,
        @NotBlank @Size(max = 4000) String content
) {}
