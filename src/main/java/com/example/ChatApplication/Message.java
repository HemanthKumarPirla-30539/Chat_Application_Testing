package com.example.ChatApplication;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;   // sender email
    private String receiver; // receiver email
    private String text;
    private LocalDateTime timestamp = LocalDateTime.now();

    public Long getId() { return id; }
    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public String getText() { return text; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public void setSender(String sender) { this.sender = sender; }
    public void setReceiver(String receiver) { this.receiver = receiver; }
    public void setText(String text) { this.text = text; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
