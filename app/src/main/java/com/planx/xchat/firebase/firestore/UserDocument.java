package com.planx.xchat.firebase.firestore;

import java.util.Map;

public class UserDocument {
    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String avatar;
    private Map<String, Boolean> friends;
    private Map<String, Boolean> rooms;

    public UserDocument() {
    }

    public UserDocument(String id, String firstName, String lastName, String fullName, String avatar, Map<String, Boolean> friends, Map<String, Boolean> rooms) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.avatar = avatar;
        this.friends = friends;
        this.rooms = rooms;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Map<String, Boolean> getFriends() {
        return friends;
    }

    public void setFriends(Map<String, Boolean> friends) {
        this.friends = friends;
    }

    public Map<String, Boolean> getRooms() {
        return rooms;
    }

    public void setRooms(Map<String, Boolean> rooms) {
        this.rooms = rooms;
    }
}
