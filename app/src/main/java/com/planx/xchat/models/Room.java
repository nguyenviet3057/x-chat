package com.planx.xchat.models;

import com.planx.xchat.firebase.database.RoomReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Room implements Serializable {
    private String id;
    private String lastChat;
    private String lastId;
    private List<User> participants;
    private String senderId;
    private String senderName;
    private String senderAvatar;
    private String receiverId;
    private String receiverName;
    private String receiverAvatar;
    private Date timestamp;

    public Room() {
        participants = new ArrayList<>();
    }

    public RoomReference toRoomReference() {
        return new RoomReference(lastChat, lastId, participants.stream()
                .collect(Collectors.toMap(User::getId, obj -> true)), senderId, senderName, senderAvatar, receiverId, receiverName, receiverAvatar, timestamp);
    }

    public Room(String id, String lastChat, String lastId, List<User> participants, String senderId, String senderName, String senderAvatar, String receiverId, String receiverName, String receiverAvatar, Date timestamp) {
        this.id = id;
        this.lastChat = lastChat;
        this.lastId = lastId;
        this.participants = participants;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderAvatar = senderAvatar;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.receiverAvatar = receiverAvatar;
        this.timestamp = timestamp;
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

    public String getLastId() {
        return lastId;
    }

    public void setLastId(String lastId) {
        this.lastId = lastId;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
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

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
