package com.planx.xchat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.planx.xchat.R;
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
    private Toolbar tbHeader;
    private RecyclerView rvRoomList;
    private ArrayList<Room> roomList;
    private RoomListAdapter roomListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User user = User.getInstance();
        user.setId(1);
        user.setFirstName("A");
        user.setAvatar("");

        db = new DatabaseHandler(this);
        try {
            db.addSampleData();
        } catch (Exception ex) {
            Log.e("Add sample data", ex.getMessage());
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.tbHeader);

        tbHeader = findViewById(R.id.tbHeader);
        rvRoomList = findViewById(R.id.rvRoomList);

        showOldRoomList();
    }

    private void showOldRoomList() {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Message>>(){}.getType();
        roomList = db.getAllRoom();
        roomListAdapter = new RoomListAdapter(this, roomList, new IOnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this, Integer.toString(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(int position) {
                Toast.makeText(MainActivity.this, Integer.toString(position), Toast.LENGTH_SHORT).show();
            }
        });
        rvRoomList.setAdapter(roomListAdapter);
        rvRoomList.setLayoutManager(new LinearLayoutManager(this));
    }
}