package com.planx.xchat.entities;

import java.util.List;

public class Room {
    private String id;
    private String lastChat;
    private int lastId;
    private List<Integer> participants;
    private int receiverId;
    private String receiverName;
    private String receiverAvatar;
    private int senderId;
    private String senderName;
    private String senderAvatar;

    public Room(String id, String lastChat, int lastId, List<Integer> participants, int receiverId, String receiverName, String receiverAvatar, int senderId, String senderName, String senderAvatar) {
        this.id = id;
        this.lastChat = lastChat;
        this.lastId = lastId;
        this.participants = participants;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.receiverAvatar = receiverAvatar;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderAvatar = senderAvatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastChat() {
        return lastChat;
    }

    public void setLastChat(String lastChat) {
        this.lastChat = lastChat;
    }

    public int getLastId() {
        return lastId;
    }

    public void setLastId(int lastId) {
        this.lastId = lastId;
    }

    public List<Integer> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Integer> participants) {
        this.participants = participants;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverAvatar() {
        return receiverAvatar;
    }

    public void setReceiverAvatar(String receiverAvatar) {
        this.receiverAvatar = receiverAvatar;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }
}
