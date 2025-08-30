package messaging.service;

import messaging.model.Message;
import messaging.repository.MessageRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    private final MessageRepository repo;
    public MessageService(MessageRepository repo) { this.repo = repo; }

    public Message send(Long senderId, Long receiverId, String content) {
        Message m = new Message();
        m.setSenderId(senderId);
        m.setReceiverId(receiverId);
        m.setContent(content.trim());
        return repo.save(m);
    }

    public Page<Message> inbox(Long userId, int page, int size) {
        return repo.findByReceiverIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
    }

    public Page<Message> thread(Long a, Long b, int page, int size) {
        return repo.findThread(a, b, PageRequest.of(page, size));
    }

}
