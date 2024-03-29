package com.planx.xchat.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

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
    private EditText etMessage;

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
        etMessage = findViewById(R.id.etMessage);
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Đo lường số dòng hiển thị trong EditText
                int lineCount = etMessage.getLineCount();

                // Lấy chiều cao của mỗi dòng văn bản
                int lineHeight = etMessage.getLineHeight();

                // Tính toán độ cao cần thiết
                int desiredHeight = lineHeight * Math.min(lineCount, 3);

                // Đặt lại độ cao của EditText
                etMessage.getLayoutParams().height = desiredHeight;
                etMessage.setLines(Math.min(lineCount, 3));
            }
        });

        showMessageList();
    }

    private void showMessageList() {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Message>>(){}.getType();
        String arr = "[{\"id\":\"J7AesfvdhOL2LUvg\",\"participants\":[1,2],\"chat\":\"Xin chào\",\"images\":[],\"senderId\":1,\"senderName\":\"A\",\"senderAvatar\":\"https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg\",\"receiverId\":2,\"receiverName\":\"B\",\"timestamp\":\"2024-03-28T15:25:50+07:00\"},{\"id\":\"8op6JU2riQ0973td\",\"participants\":[1,2],\"chat\":\"Ok, chào lại\",\"images\":[],\"senderId\":2,\"senderName\":\"B\",\"senderAvatar\":\"https://www.refugee-action.org.uk/wp-content/uploads/2016/10/anonymous-user.png\",\"receiverId\":1,\"receiverName\":\"A\",\"timestamp\":\"2024-03-28T15:25:51+07:00\"},{\"id\":\"krcyU03muWJe3eTJ\",\"participants\":[1,2],\"chat\":\"Hỏi lần đầu\",\"images\":[],\"senderId\":1,\"senderAvatar\":\"https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg\",\"senderName\":\"A\",\"receiverId\":2,\"receiverName\":\"B\",\"timestamp\":\"2024-03-28T15:25:52+07:00\"},{\"id\":\"0UdhY9S4FecqpHmU\",\"participants\":[1,2],\"chat\":\"Hỏi tiếp\",\"images\":[],\"senderId\":1,\"senderName\":\"A\",\"senderAvatar\":\"https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg\",\"receiverId\":2,\"receiverName\":\"B\",\"timestamp\":\"2024-03-28T15:25:53+07:00\"}]";
        messageList = gson.fromJson(arr, listType);
        messageListAdapter = new MessageListAdapter(this, messageList);
        rvMessageList.setAdapter(messageListAdapter);
//        rvMessageList.addItemDecoration(new DividerItemDecoration(rvMessageList.getContext(), DividerItemDecoration.VERTICAL));
        rvMessageList.setLayoutManager(new LinearLayoutManager(this));
    }
}