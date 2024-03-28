package com.planx.xchat.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.planx.xchat.R;
import com.planx.xchat.adapters.MessageListAdapter;
import com.planx.xchat.entities.Message;
import com.planx.xchat.entities.User;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Toolbar tbHeader;
    private RecyclerView rvMessageList;
    private ArrayList<Message> messageList;
    private MessageListAdapter messageListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User user = User.getInstance();
        user.setId(1);
        user.setName("A");
        user.setAvatar("");

        setContentView(R.layout.activity_main);
        tbHeader = findViewById(R.id.tbHeader);
        setSupportActionBar(tbHeader);
        rvMessageList = findViewById(R.id.rvMessageList);

        showMessageList();
    }

    private void showMessageList() {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Message>>(){}.getType();
        String arr = "[{\"id\":\"J7AesfvdhOL2LUvg\",\"participants\":[1,2],\"chat\":\"Xin chào\",\"images\":[],\"senderId\":1,\"senderName\":\"A\",\"receiverId\":2,\"receiverName\":\"B\",\"timestamp\":\"2024-03-28T15:25:50+07:00\"},{\"id\":\"8op6JU2riQ0973td\",\"participants\":[1,2],\"chat\":\"Ok, chào lại\",\"images\":[],\"senderId\":2,\"senderName\":\"B\",\"receiverId\":1,\"receiverName\":\"A\",\"timestamp\":\"2024-03-28T15:25:51+07:00\"},{\"id\":\"krcyU03muWJe3eTJ\",\"participants\":[1,2],\"chat\":\"Hỏi lần đầu\",\"images\":[],\"senderId\":1,\"senderName\":\"A\",\"receiverId\":2,\"receiverName\":\"B\",\"timestamp\":\"2024-03-28T15:25:52+07:00\"},{\"id\":\"0UdhY9S4FecqpHmU\",\"participants\":[1,2],\"chat\":\"Hỏi tiếp\",\"images\":[],\"senderId\":1,\"senderName\":\"A\",\"receiverId\":2,\"receiverName\":\"B\",\"timestamp\":\"2024-03-28T15:25:53+07:00\"}]";
        messageList = gson.fromJson(arr, listType);
        messageListAdapter = new MessageListAdapter(this, messageList);
        rvMessageList.setAdapter(messageListAdapter);
//        rvMessageList.addItemDecoration(new DividerItemDecoration(rvMessageList.getContext(), DividerItemDecoration.VERTICAL));
        rvMessageList.setLayoutManager(new LinearLayoutManager(this));
    }
}