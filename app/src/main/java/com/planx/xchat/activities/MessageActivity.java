package com.planx.xchat.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.planx.xchat.R;
import com.planx.xchat.adapters.DateLongIntFormatAdapter;
import com.planx.xchat.adapters.MessageListAdapter;
import com.planx.xchat.databinding.ActivityMessageBinding;
import com.planx.xchat.sqlite.Message;
import com.planx.xchat.entities.User;
import com.planx.xchat.extensions.LinearLayoutIOnItemClickListener;
import com.planx.xchat.sqlite.DatabaseHandler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

public class MessageActivity extends AppCompatActivity {

    private DatabaseHandler db;
    private ActivityMessageBinding binding;
    private Toolbar tbHeader;
    private LinearLayoutIOnItemClickListener llMessageHistoryContainer;
    private RecyclerView rvMessageList;
    private EditText etMessage;

    private ArrayList<Message> messageList;
    private MessageListAdapter messageListAdapter;

    private int roomIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DatabaseHandler(this);
        try {
            db.addSampleData();
        } catch (Exception ex) {
            Log.e("Add sample data", ex.getMessage());
        }

        roomIndex = 0;

        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.tbHeader);

        tbHeader = findViewById(R.id.tbHeader);

        llMessageHistoryContainer = findViewById(R.id.llMessageHistoryContainer);
        llMessageHistoryContainer.setOnClickListener(v -> {
            // Hide keyboard
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null && getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
        llMessageHistoryContainer.setOnLongClickListener(v -> {
            Toast.makeText(this, messageList.get(llMessageHistoryContainer.getPosition()).getChat(), Toast.LENGTH_SHORT).show();
            return true;
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

        showOldMessageList();
    }

    private void showOldMessageList() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new DateLongIntFormatAdapter());
        Gson gson = gsonBuilder.create();
        Type listType = new TypeToken<ArrayList<Message>>(){}.getType();
        messageList = gson.fromJson(db.getAllRoom().get(roomIndex).getMessageListJson(), listType);
        messageListAdapter = new MessageListAdapter(this, messageList, llMessageHistoryContainer);
        rvMessageList.setAdapter(messageListAdapter);
        rvMessageList.setLayoutManager(new LinearLayoutManager(this));
    }
}