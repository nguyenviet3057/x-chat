package com.planx.xchat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.planx.xchat.adapters.FriendListAdapter;
import com.planx.xchat.adapters.RoomListAdapter;
import com.planx.xchat.databinding.ActivityMainBinding;
import com.planx.xchat.entities.Message;
import com.planx.xchat.entities.User;
import com.planx.xchat.interfaces.IOnItemClickListener;
import com.planx.xchat.sqlite.Room;
import com.planx.xchat.sqlite.DatabaseHandler;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseHandler db;
    private ActivityMainBinding binding;
    private ArrayList<Room> roomList;
    private RoomListAdapter roomListAdapter;
    private ArrayList<com.planx.xchat.sqlite.User> friendList;
    private FriendListAdapter friendListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User user = User.getInstance();
//        user.setId(1);
//        user.setFirstName("A");
//        user.setAvatar("");

        db = new DatabaseHandler(this);
        try {
//            db.addSampleData();
        } catch (Exception ex) {
            Log.e("Add sample data", ex.getMessage());
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        showOldRoomList();
        showOldFriendList();
    }

    private void showOldFriendList() {
        friendList = db.getAllFriends();
        friendListAdapter = new FriendListAdapter(this, friendList, new IOnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this, friendList.get(position).getFirstName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(int position) {

            }
        });
        binding.rvFriendList.setAdapter(friendListAdapter);
        binding.rvFriendList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void showOldRoomList() {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Message>>(){}.getType();
        roomList = db.getAllRooms();
        roomListAdapter = new RoomListAdapter(this, roomList, new IOnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                intent.putExtra("roomId", roomList.get(position).getId()); // Gửi dữ liệu với key "key"
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(int position) {
                Toast.makeText(MainActivity.this, Integer.toString(position), Toast.LENGTH_SHORT).show();
            }
        });
        binding.rvRoomList.setAdapter(roomListAdapter);
        binding.rvRoomList.setLayoutManager(new LinearLayoutManager(this));
    }
}