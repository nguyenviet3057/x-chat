package com.planx.xchat.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {

    private Context context;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "xchat";

    public DatabaseHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ROOMS_TABLE = "CREATE TABLE rooms(" +
                "id TEXT PRIMARY KEY," +
                "lastChat TEXT," +
                "lastId INTEGER," +
                "participants TEXT," +
                "senderId INTEGER," +
                "senderName TEXT," +
                "senderAvatar TEXT," +
                "receiverId INTEGER," +
                "receiverName TEXT," +
                "receiverAvatar TEXT," +
                "messageListJson TEXT" +
                ")";
        db.execSQL(CREATE_ROOMS_TABLE);

        String CREATE_FRIENDS_TABLE = "CREATE TABLE friends(" +
                "id INTEGER PRIMARY KEY," +
                "firstName TEXT," +
                "lastName TEXT," +
                "fullName TEXT," +
                "avatar TEXT" +
                ")";
        db.execSQL(CREATE_FRIENDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

//    public void addSampleData() throws Exception {
//        SQLiteDatabase db = null;
//        try {
//            db = this.getWritableDatabase();
//
//            ArrayList<Room> roomList = FakerData.generateRoomList();
//            for (int i = 0; i < roomList.size(); i++) {
//                Room room = roomList.get(i);
//                ContentValues values = new ContentValues();
//                values.put("id", room.getId()); // Contact Name
//                values.put("lastChat", room.getLastChat());
//                values.put("lastId", room.getLastId());
//                values.put("participants", room.getParticipants().toString());
//                values.put("senderId", room.getSenderId());
//                values.put("senderName", room.getSenderName());
//                values.put("senderAvatar", room.getSenderAvatar());
//                values.put("receiverId", room.getReceiverId());
//                values.put("receiverName", room.getReceiverName());
//                values.put("receiverAvatar", room.getReceiverAvatar());
//
////                room.setMessageListJson(Utils.loadJSONArrayFromAsset(context, "message_list.json") != null ? Utils.loadJSONArrayFromAsset(context, "message_list.json").get(i).toString() : "");
//                values.put("messageListJson", room.getMessageListJson());
//
//                db.insert("rooms", "", values);
//            }
//
//            ArrayList<com.planx.xchat.entities.User> friendList = FakerData.fakerUserList;
//            for (int i = 0; i < friendList.size(); i++) {
//                com.planx.xchat.entities.User friend = friendList.get(i);
//                ContentValues values = new ContentValues();
//                values.put("id", friend.getId()); // Contact Name
//                values.put("firstName", friend.getFirstName());
//                values.put("lastName", friend.getLastName());
//                values.put("fullName", friend.getFullName());
//                values.put("avatar", friend.getAvatar());
//
//                db.insert("friends", "", values);
//            }
//
//
//        } catch (Exception ex) {
//            throw new Exception(ex.getMessage());
//        } finally {
//            db.close(); // Closing database connection
//        }
//    }

//    public ArrayList<Room> getAllRooms() {
//        ArrayList<Room> roomList = new ArrayList<>();
//        String selectQuery = "SELECT  * FROM rooms WHERE senderId = '" + MainUser.getInstance().getId() + "' OR receiverId = '" + MainUser.getInstance().getId() + "'";
//
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        try {
//            Cursor cursor = db.rawQuery(selectQuery, null);
//
//            if (cursor.moveToFirst()) {
//                do {
//                    Map<String, Boolean> participants = Arrays.stream(cursor.getString(3).split(","))
//                            .collect(Collectors.toMap(s -> s.split("=")[0], s -> s.split("=").length == 2 && Boolean.parseBoolean(s.split("=")[1])));
//                    Room room = new Room(
//                            cursor.getString(0),
//                            cursor.getString(1),
//                            cursor.getString(2),
//                            participants,
//                            cursor.getString(4),
//                            cursor.getString(5),
//                            cursor.getString(6),
//                            cursor.getString(7),
//                            cursor.getString(8),
//                            cursor.getString(9),
//                            cursor.getString(10)
//                    );
//                    roomList.add(room);
//                } while (cursor.moveToNext());
//            }
//
//            return roomList;
//        } catch (SQLiteException ex) {
//            Log.e("getAllRooms", ex.getMessage());
//            return new ArrayList<>();
//        }
//    }

//    public Room findRoomById(String id) {
//        String selectQuery = "SELECT  * FROM rooms WHERE id = '" + id + "' AND (senderId = '" + MainUser.getInstance().getId() + "' OR receiverId = '" + MainUser.getInstance().getId() + "')";
//
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        try {
//            Cursor cursor = db.rawQuery(selectQuery, null);
//
//            Room room = new Room();
//            if (cursor.moveToFirst()) {
//                do {
//                    room.setId(cursor.getString(0));
//                    room.setLastChat(cursor.getString(1));
//                    room.setLastId(cursor.getString(2));
//                    room.setSenderId(cursor.getString(3));
//                    room.setSenderName(cursor.getString(4));
//                    room.setSenderAvatar(cursor.getString(5));
//                    room.setReceiverId(cursor.getString(6));
//                    room.setReceiverName(cursor.getString(7));
//                    room.setReceiverAvatar(cursor.getString(8));
//                    room.setMessageListJson(cursor.getString(9));
//                } while (cursor.moveToNext());
//            }
//
//            return room;
//        } catch (SQLiteException ex) {
//            Log.e("findRoomById", ex.getMessage());
//            return new Room();
//        }
//    }

//    public ArrayList<com.planx.xchat.entities.User> getAllFriends() {
//        ArrayList<com.planx.xchat.entities.User> friendList = new ArrayList<>();
//        String selectQuery = "SELECT * FROM friends WHERE id != '" + MainUser.getInstance().getId() + "'";
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        try {
//            Cursor cursor = db.rawQuery(selectQuery, null);
//
//            if (cursor.moveToFirst()) {
//                do {
//                    com.planx.xchat.entities.User friend = new com.planx.xchat.entities.User();
//                    friend.setId(cursor.getString(0));
//                    friend.setFirstName(cursor.getString(1));
//                    friend.setLastName(cursor.getString(2));
//                    friend.setFullName(cursor.getString(3));
//                    friend.setAvatar(cursor.getString(4));
//                    friendList.add(friend);
//                } while (cursor.moveToNext());
//            }
//
//            return friendList;
//        } catch (SQLiteException ex) {
//            Log.e("getAllFriends", ex.getMessage());
//            return new ArrayList<>();
//        }
//    }
}
