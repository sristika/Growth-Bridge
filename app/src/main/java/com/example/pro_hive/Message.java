package com.example.pro_hive;

import java.util.Date;

public class Message {
    private String messageId;
    private String senderId;
    private String text;
    private Date timestamp;

    public Message() {
    }

    public Message(String messageId, String senderId, String text, Date timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getText() {
        return text;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}

