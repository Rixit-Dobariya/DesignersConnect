package com.example.designersconnect.Models;

public class Message {
    String message;
    String sender;
    String receiver;
    long timestamp;
    String messageId;

    Message(){

    }

    public String getMessageId() {
        return messageId;
    }



    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Message(String message, String sender, String receiver, String messageId) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = System.currentTimeMillis();
        this.messageId = messageId;
    }
}
