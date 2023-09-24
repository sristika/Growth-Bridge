package com.example.pro_hive;

import java.util.List;

public class Chat {
    private String chatId;
    private boolean isGroupChat;
    private String chatName;
    private List<String> members;
    private List<Message> messages;

    public Chat() {
    }

    public Chat(String chatId, boolean isGroupChat, String chatName, List<String> members, List<Message> messages) {
        this.chatId = chatId;
        this.isGroupChat = isGroupChat;
        this.chatName = chatName;
        this.members = members;
        this.messages = messages;
    }

    public Chat(String chatId, boolean isGroupChat, String chatName, List<String> members) {
        this.chatId = chatId;
        this.isGroupChat = isGroupChat;
        this.chatName = chatName;
        this.members = members;
    }

    public String getChatId() {
        return chatId;
    }

    public boolean isGroupChatfun() {
        return isGroupChat == true;
    }

    public String getChatName() {
        return chatName;
    }

    public List<String> getMembers() {
        return members;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setGroupChat(boolean groupChat) {
        isGroupChat = groupChat;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}

