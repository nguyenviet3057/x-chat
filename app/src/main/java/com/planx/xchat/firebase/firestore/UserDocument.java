package com.planx.xchat.firebase.firestore;

import java.util.List;
import java.util.Map;

public class UserDocument {
    private String firstName;
    private String lastName;
    private String fullName;
    private String avatar;
    private List<String> friends;
    private List<String> rooms;

    public UserDocument() {
    }

    public UserDocument(String firstName, String lastName, String fullName, String avatar, List<String> friends, List<String> rooms) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.avatar = avatar;
        this.friends = friends;
        this.rooms = rooms;
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

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<String> getRooms() {
        return rooms;
    }

    public void setRooms(List<String> rooms) {
        this.rooms = rooms;
    }
}
