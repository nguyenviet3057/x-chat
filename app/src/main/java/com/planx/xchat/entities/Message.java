package com.planx.xchat.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Message {
    private String id;
    private List<Integer> participants;
    private String chat;
    private ArrayList<String> images;
    private int senderId;
    private String senderName;
    private String senderAvatar;
    private int receiverId;
    private String receiverName;
    private Date timestamp;

    public Message(String id, List<Integer> participants, String chat, ArrayList<String> images, int senderId, String senderName, String senderAvatar, int receiverId, String receiverName, Date timestamp) {
        this.id = id;
        this.participants = participants;
        this.chat = chat;
        this.images = images;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderAvatar = senderAvatar;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Integer> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Integer> participants) {
        this.participants = participants;
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
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

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
