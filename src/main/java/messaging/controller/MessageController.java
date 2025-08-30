package messaging.controller;// messaging.controller.MessageController
import jakarta.validation.Valid;
import messaging.api.SendMessageRequest;
import messaging.api.MessageResponse;
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

    @PostMapping(path="/send", consumes= MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
    public MessageResponse send(@RequestBody @Valid SendMessageRequest body) {
        Message m = service.send(body.senderId(), body.receiverId(), body.content());
        return new MessageResponse(m.getId(), m.getSenderId(), m.getReceiverId(),
                m.getContent(), m.getCreatedAt().toString(), m.isRead());
    }

    @GetMapping(path="/receive", produces=MediaType.APPLICATION_JSON_VALUE)
    public Page<MessageResponse> receive(@RequestParam Long userId,
                                         @RequestParam(defaultValue="0") int page,
                                         @RequestParam(defaultValue="50") int size) {
        return service.inbox(userId, page, size).map(m ->
                new MessageResponse(m.getId(), m.getSenderId(), m.getReceiverId(),
                        m.getContent(), m.getCreatedAt().toString(), m.isRead())
        );
    }
    @GetMapping(path="/thread", produces=MediaType.APPLICATION_JSON_VALUE)
    public Page<MessageResponse> thread(@RequestParam Long a, @RequestParam Long b,
                                        @RequestParam(defaultValue="0") int page,
                                        @RequestParam(defaultValue="50") int size) {
        return service.thread(a, b, page, size).map(m ->
                new MessageResponse(m.getId(), m.getSenderId(), m.getReceiverId(),
                        m.getContent(), m.getCreatedAt().toString(), m.isRead())
        );
    }
}
