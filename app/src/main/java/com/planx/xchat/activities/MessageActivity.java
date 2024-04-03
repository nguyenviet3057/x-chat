package com.planx.xchat.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewTreeObserver;
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
import com.planx.xchat.extensions.LinearLayoutIOnItemClickListener;
import com.planx.xchat.sqlite.DatabaseHandler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MessageActivity extends AppCompatActivity {

    private DatabaseHandler db;
    private ActivityMessageBinding binding;

    private ArrayList<Message> messageList;
    private MessageListAdapter messageListAdapter;

    private String roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DatabaseHandler(this);
        try {
//            db.addSampleData();
        } catch (Exception ex) {
            Log.e("Add sample data", ex.getMessage());
        }

        Intent intent = getIntent();
        if (intent != null) {
            this.roomId = intent.getStringExtra("roomId");
        }

        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.llMessageHistoryContainer.setOnClickListener(v -> {
            // Hide keyboard
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null && getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
        binding.llMessageHistoryContainer.setOnLongClickListener(v -> {
            Toast.makeText(this, messageList.get(binding.llMessageHistoryContainer.getPosition()).getChat(), Toast.LENGTH_SHORT).show();
            return true;
        });

        int etMessageMaxLines = binding.etMessage.getMaxLines();
        binding.etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int lineCount = binding.etMessage.getLineCount();
                int lineHeight = binding.etMessage.getLineHeight();

                binding.etMessage.getLayoutParams().height = lineHeight * Math.min(lineCount, etMessageMaxLines);
                binding.etMessage.setLines(Math.min(lineCount, etMessageMaxLines));
            }
        });

        showOldMessageList();
    }

    private void showOldMessageList() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new DateLongIntFormatAdapter());
        Gson gson = gsonBuilder.create();
        Type listType = new TypeToken<ArrayList<Message>>(){}.getType();
        messageList = gson.fromJson(db.findRoomById(roomId).getMessageListJson(), listType); // Sorted time_stamp descending
        Collections.reverse(messageList); // Reverse to time_stamp ascending
        messageListAdapter = new MessageListAdapter(this, messageList, binding.llMessageHistoryContainer);
        binding.rvMessageList.setAdapter(messageListAdapter);

//        rvMessageList.addItemDecoration(new DividerItemDecoration(rvMessageList.getContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        binding.rvMessageList.setLayoutManager(layoutManager);
    }
}