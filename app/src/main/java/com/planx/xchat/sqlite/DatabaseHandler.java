package com.planx.xchat.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.planx.xchat.entities.Message;
import com.planx.xchat.entities.User;
import com.planx.xchat.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

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
                "senderId INTEGER," +
                "senderName TEXT," +
                "senderAvatar TEXT," +
                "receiverId INTEGER," +
                "receiverName TEXT," +
                "receiverAvatar TEXT," +
                "messageListJson TEXT" +
                ")";
        db.execSQL(CREATE_ROOMS_TABLE);

        // Sample data
        try {
            addSampleData();
        } catch (Exception ex) {
            Log.e("Add sample data to SQLite", ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void addSampleData() throws Exception {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();

            Type listType = new TypeToken<ArrayList<Room>>(){}.getType();
            Gson gson = new Gson();

//            ArrayList<Room> roomList = gson.fromJson(Utils.readJsonFileFromAssets(context, "room_list.json"), listType);
            ArrayList<Room> roomList = FakerData.generateRoomList();
            for (int i = 0; i < roomList.size(); i++) {
                Room room = roomList.get(i);
                ContentValues values = new ContentValues();
                values.put("id", room.getId()); // Contact Name
                values.put("lastChat", room.getLastChat());
                values.put("lastId", room.getLastId());
                values.put("senderId", room.getSenderId());
                values.put("senderName", room.getSenderName());
                values.put("senderAvatar", room.getSenderAvatar());
                values.put("receiverId", room.getReceiverId());
                values.put("receiverName", room.getReceiverName());
                values.put("receiverAvatar", room.getReceiverAvatar());

//                room.setMessageListJson(Utils.loadJSONArrayFromAsset(context, "message_list.json") != null ? Utils.loadJSONArrayFromAsset(context, "message_list.json").get(i).toString() : "");
                values.put("messageListJson", room.getMessageListJson());

                db.insert("rooms", "", values);
            }


        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        } finally {
            db.close(); // Closing database connection
        }
    }

    public ArrayList<Room> getAllRoom() {
        ArrayList<Room> roomList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM rooms WHERE senderId = " + User.getInstance().getId() + " OR receiverId = " + User.getInstance().getId();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Room room = new Room(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getInt(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9)
                );
                roomList.add(room);
            } while (cursor.moveToNext());
        }

        return roomList;
    }
}
