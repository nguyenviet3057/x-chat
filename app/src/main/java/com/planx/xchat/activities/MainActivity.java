package com.planx.xchat.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.planx.xchat.R;
import com.planx.xchat.adapters.MessageListAdapter;
import com.planx.xchat.entities.Message;
import com.planx.xchat.entities.User;
import com.planx.xchat.extensions.LinearLayoutIOnItemClickListener;
import com.planx.xchat.interfaces.IOnItemClickListener;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Toolbar tbHeader;
    private LinearLayoutIOnItemClickListener llMessageHistoryContainer;
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

        llMessageHistoryContainer = findViewById(R.id.llMessageHistoryContainer);
        llMessageHistoryContainer.setOnClickListener(v -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null && getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        rvMessageList = findViewById(R.id.rvMessageList);

        etMessage = findViewById(R.id.etMessage);
        int etMessageMaxLines = etMessage.getMaxLines();
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int lineCount = etMessage.getLineCount();
                int lineHeight = etMessage.getLineHeight();

                etMessage.getLayoutParams().height = lineHeight * Math.min(lineCount, etMessageMaxLines);
                etMessage.setLines(Math.min(lineCount, etMessageMaxLines));
            }
        });

        showMessageList();
    }

    private void showMessageList() {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Message>>(){}.getType();
        String arr = "[{\"id\":\"J7AesfvdhOL2LUvg\",\"participants\":[1,2],\"chat\":\"Xin chào\",\"images\":[],\"senderId\":1,\"senderName\":\"A\",\"senderAvatar\":\"https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg\",\"receiverId\":2,\"receiverName\":\"B\",\"timestamp\":\"2024-03-28T15:25:50+07:00\"},{\"id\":\"8op6JU2riQ0973td\",\"participants\":[1,2],\"chat\":\"Ok, chào lại\",\"images\":[],\"senderId\":2,\"senderName\":\"B\",\"senderAvatar\":\"https://scontent.fhan2-4.fna.fbcdn.net/v/t39.30808-6/349183218_771967877937300_2862042189838733404_n.jpg?_nc_cat=100&ccb=1-7&_nc_sid=5f2048&_nc_eui2=AeE-8m2jeSoWF2BOFyCCXLHWzH2Hr7d9MebMfYevt30x5k2IAXnxRc3oxfKwCxe9kAFtC8PcMcKzrTw8qQY1vMaZ&_nc_ohc=jKygHknYrNgAX99OAWO&_nc_ht=scontent.fhan2-4.fna&oh=00_AfAa2FPnI0Q0F5mwQQqFEdYABy7M0HAyyNxnIHFqeUbPAg&oe=660D36A7\",\"receiverId\":1,\"receiverName\":\"A\",\"timestamp\":\"2024-03-28T15:25:51+07:00\"},{\"id\":\"krcyU03muWJe3eTJ\",\"participants\":[1,2],\"chat\":\"Hỏi lần đầu\",\"images\":[],\"senderId\":1,\"senderAvatar\":\"https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg\",\"senderName\":\"A\",\"receiverId\":2,\"receiverName\":\"B\",\"timestamp\":\"2024-03-28T15:25:52+07:00\"},{\"id\":\"0UdhY9S4FecqpHmU\",\"participants\":[1,2],\"chat\":\"Hỏi tiếp\",\"images\":[],\"senderId\":1,\"senderName\":\"A\",\"senderAvatar\":\"https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg\",\"receiverId\":2,\"receiverName\":\"B\",\"timestamp\":\"2024-03-28T15:25:53+07:00\"}]";
        messageList = gson.fromJson(arr, listType);
        messageListAdapter = new MessageListAdapter(this, messageList, llMessageHistoryContainer);
        rvMessageList.setAdapter(messageListAdapter);
        rvMessageList.setLayoutManager(new LinearLayoutManager(this));
    }
}