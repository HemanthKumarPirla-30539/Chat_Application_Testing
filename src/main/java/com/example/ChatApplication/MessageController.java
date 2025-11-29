package com.example.ChatApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin("*")
public class MessageController {

    @Autowired
    private MessageRepository messageRepo;

    @PostMapping("/send")
    public Map<String, String> sendMessage(@RequestBody Message msg) {
        msg.setTimestamp(LocalDateTime.now());
        messageRepo.save(msg);
        Map<String, String> res = new HashMap<>();
        res.put("status", "success");
        return res;
    }

    @GetMapping("/load")
    public List<Message> loadMessages(@RequestParam String user1, @RequestParam String user2) {
        return messageRepo.getConversation(user1, user2);
    }
}
