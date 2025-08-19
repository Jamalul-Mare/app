package messaging.controller;

import messaging.model.Message;
import messaging.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public Message sendMessage(@RequestBody Message message) {
        return messageService.sendMessage(message);
    }

    @GetMapping("/between")
    public List<Message> getMessagesBetween(
            @RequestParam Long senderId,
            @RequestParam Long receiverId) {
        return messageService.getMessagesBetweenUsers(senderId, receiverId);
    }

    @GetMapping("/received")
    public List<Message> getMessagesForUser(@RequestParam Long userId) {
        return messageService.getMessagesForUser(userId);
    }
}
