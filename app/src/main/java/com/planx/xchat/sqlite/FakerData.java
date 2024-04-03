package com.planx.xchat.sqlite;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FakerData {
    public static ArrayList<Room> generateRoomList() throws JsonProcessingException {
        Faker faker = new Faker();
        ObjectMapper objectMapper = new ObjectMapper();

        // Fake user list
        String[] avatarURLs = {
                "https://img.freepik.com/free-psd/3d-illustration-human-avatar-profile_23-2150671142.jpg",
                "https://img.freepik.com/free-psd/3d-illustration-human-avatar-profile_23-2150671122.jpg",
                "https://img.freepik.com/freie-psd/3d-darstellung-eines-menschlichen-avatars-oder-profils_23-2150671134.jpg",
                "https://img.freepik.com/freie-psd/3d-darstellung-eines-menschlichen-avatars-oder-profils_23-2150671151.jpg",
                "https://img.freepik.com/free-psd/3d-illustration-human-avatar-profile_23-2150671118.jpg",
                "https://img.freepik.com/free-psd/3d-illustration-human-avatar-profile_23-2150671149.jpg?w=360",
                "https://img.freepik.com/free-psd/3d-illustration-human-avatar-profile_23-2150671140.jpg",
                "https://img.freepik.com/freie-psd/3d-darstellung-eines-menschlichen-avatars-oder-profils_23-2150671136.jpg",
                "https://img.freepik.com/psd-premium/3d-darstellung-eines-menschlichen-avatars-oder-profils_23-2150671171.jpg",
                "https://img.freepik.com/freie-psd/3d-darstellung-eines-menschlichen-avatars-oder-profils_23-2150671159.jpg"
        };
        ArrayList<String> avatarURLList = new ArrayList<>();
        Collections.addAll(avatarURLList, avatarURLs);

        ArrayList<User> userList = new ArrayList<>();
        for (int userIndex = 1; userIndex <= 10; userIndex++) {
            User user = new User();
            user.setId(userIndex);
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().lastName());
            user.setFullName(user.getFirstName() + " " + user.getLastName());

            String avatarUrl = avatarURLList.get(faker.random().nextInt(0, avatarURLList.size() - 1));
            avatarURLList.remove(avatarUrl);
            user.setAvatar(avatarUrl);

            userList.add(user);
        }

        com.planx.xchat.entities.User.getInstance().setId(userList.get(0).getId());
        com.planx.xchat.entities.User.getInstance().setFirstName(userList.get(0).getFirstName());
        com.planx.xchat.entities.User.getInstance().setLastName(userList.get(0).getLastName());
        com.planx.xchat.entities.User.getInstance().setFullName(userList.get(0).getFullName());
        com.planx.xchat.entities.User.getInstance().setAvatar(userList.get(0).getAvatar());

        // Fake room list
        int[][] roomParticipants = {
                {}, // Empty first array because following code start index at 1
                {1, 2},
                {2, 3},
                {1, 5}
        };
        ArrayList<Room> roomList = new ArrayList<>();
        for (int roomIndex = 1; roomIndex <= 3; roomIndex++) {
            Room room = new Room();
            room.setId(UUID.randomUUID().toString());

            ArrayList<Message> messageList = new ArrayList<>();
            int[] roomParticipant = roomParticipants[roomIndex];
            for (int messageIndex = 1; messageIndex <= 30; messageIndex++) {
                Message message = new Message();
                message.setId(UUID.randomUUID().toString());
                message.setChat(faker.lorem().sentence(16, 20));
                message.setImages(new ArrayList<>());
                message.setSenderId(roomParticipant[faker.random().nextInt(0, 1)]);
                message.setSenderName(userList.get(message.getSenderId()).getFirstName());
                message.setSenderAvatar(userList.get(message.getSenderId()).getAvatar());
                message.setReceiverId(message.getSenderId() == roomParticipant[0] ? roomParticipant[1] : roomParticipant[0]);
                message.setReceiverName(userList.get(message.getReceiverId()).getFirstName());
                message.setReceiverAvatar(userList.get(message.getReceiverId()).getAvatar());
                message.setTimestamp(faker.date().between(faker.date().past(20, TimeUnit.DAYS), faker.date().past(1, TimeUnit.SECONDS))); //
//                message.setTimestamp(Date.from(Instant.now()));

                messageList.add(message);
            }
            messageList.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));

            room.setLastChat(messageList.get(0).getChat());
            room.setLastId(messageList.get(0).getSenderId());
            room.setSenderId(messageList.get(0).getSenderId());
            room.setSenderName(messageList.get(0).getSenderName());
            room.setSenderAvatar(messageList.get(0).getSenderAvatar());
            room.setReceiverId(messageList.get(0).getReceiverId());
            room.setReceiverName(messageList.get(0).getReceiverName());
            room.setReceiverAvatar(messageList.get(0).getReceiverAvatar());

            room.setMessageListJson(objectMapper.writeValueAsString(messageList));

            roomList.add(room);
        }

        return roomList;
    }
}
