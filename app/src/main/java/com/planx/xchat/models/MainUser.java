package com.planx.xchat.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainUser {
    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String avatar;
    private boolean isOnline;
    private List<String> friends;
    private List<String> rooms;
    private String jwtToken;

    private static MainUser instance;

    private MainUser() {
        friends = new ArrayList<>();
        rooms = new ArrayList<>();
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("firstName", firstName);
        result.put("lastName", lastName);
        result.put("fullName", fullName);
        result.put("avatar", avatar);
        result.put("isOnline", isOnline);
        result.put("friends", friends);
        result.put("rooms", rooms);

        return result;
    }

    public static MainUser getInstance() {
        if (instance == null) {
            instance = new MainUser();
        }
        return instance;
    }

    public void setInstance(MainUser instance) {
        MainUser.instance = instance;
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

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public List<String> getRooms() {
        return rooms;
    }

    public void setRooms(List<String> rooms) {
        this.rooms = rooms;
    }

    public User toSQLiteUser() {
        return new User(id, firstName, lastName, fullName, avatar, isOnline);
    }
}
