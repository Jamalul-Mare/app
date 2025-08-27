package messaging.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import messaging.model.Message;
import messaging.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api1")
public class MessageController {
    private final MessageService service;
    public MessageController(MessageService service) { this.service = service; }

    record SendBody(@NotNull Long senderId, @NotNull Long receiverId, @NotBlank String content) {}
    record MessageResponse(Long id, Long senderId, Long receiverId, String content, String createdAt) {}

    @PostMapping(path="/send", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
    public MessageResponse send(@RequestBody SendBody body) {
        Message m = service.send(body.senderId(), body.receiverId(), body.content());
        return new MessageResponse(m.getId(), m.getSenderId(), m.getReceiverId(), m.getContent(),
                m.getCreatedAt().toString());
    }

    @GetMapping(path="/receive", produces=MediaType.APPLICATION_JSON_VALUE)
    public Page<MessageResponse> receive(@RequestParam Long userId,
                                         @RequestParam(defaultValue="0") int page,
                                         @RequestParam(defaultValue="20") int size) {
        return service.inbox(userId, page, size).map(m ->
                new MessageResponse(m.getId(), m.getSenderId(), m.getReceiverId(), m.getContent(),
                        m.getCreatedAt().toString())
        );
    }
}
