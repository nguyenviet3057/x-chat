package com.planx.xchat.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.planx.xchat.R;
import com.planx.xchat.XChat;
import com.planx.xchat.adapters.DateLongIntFormatAdapter;
import com.planx.xchat.adapters.MessageListAdapter;
import com.planx.xchat.databinding.ActivityMessageBinding;
import com.planx.xchat.models.MainUser;
import com.planx.xchat.firebase.database.RoomReference;
import com.planx.xchat.models.Message;
import com.planx.xchat.sqlite.DatabaseHandler;
import com.planx.xchat.models.Room;
import com.planx.xchat.models.User;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomActivity extends AppCompatActivity {

    private DatabaseHandler db;
    private ActivityMessageBinding binding;

    private ArrayList<Message> messageList;
    private MessageListAdapter messageListAdapter;
    private String roomId;
    private Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        messageList = new ArrayList<>();

        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            if (intent.getStringExtra(getResources().getString(R.string.Main2MessageRoomId)) != null) {
                roomId = intent.getStringExtra(getResources().getString(R.string.Main2MessageRoomId));

                XChat.database.getReference().child(XChat.refRooms).child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        RoomReference roomReference = snapshot.getValue(RoomReference.class);
                        room = roomReference.toSQLiteRoom();
                        room.setId(roomId);
                        room.setLastId(MainUser.getInstance().getId());
                        room.getParticipants().add(MainUser.getInstance().toSQLiteUser());
                        room.setSenderId(MainUser.getInstance().getId());
                        room.setSenderName(MainUser.getInstance().getFirstName());
                        room.setSenderAvatar(MainUser.getInstance().getAvatar());
                        List<String> receiverIds = new ArrayList<>(roomReference.getParticipants().keySet());
                        receiverIds.remove(MainUser.getInstance().getId());

                        if (receiverIds.size() > 0) {
                            XChat.firestore.collection(XChat.colUsers).whereIn(FieldPath.documentId(), receiverIds).get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot docUser :
                                            task.getResult().getDocuments()) {
                                        room.getParticipants().add(docUser.toObject(User.class));
                                    }

                                    if (room.getParticipants().size() == 2) { // Only main user and their friend (two-person room)
                                        room.setReceiverId(room.getParticipants().get(0).getId());
                                        room.setReceiverName(room.getParticipants().get(0).getFirstName());
                                        room.setReceiverAvatar(room.getParticipants().get(0).getAvatar());
                                    }
                                    showMessageList();
                                } else {
                                    Toast.makeText(RoomActivity.this, "Error getting participants", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        } else {
//            this.roomKey = db.getAllRooms().get(0).getId();
            Toast.makeText(this, getResources().getString(R.string.roomNotFound), Toast.LENGTH_LONG).show();
        }

//        room = db.findRoomById(roomKey);
//        if (room.getSenderId().equals(MainUser.getInstance().getId())) {
//            receiver = new com.planx.xchat.entities.User(room.getReceiverId(), room.getReceiverName(), "", "", room.getReceiverAvatar());
//        } else {
//            receiver = new com.planx.xchat.entities.User(room.getSenderId(), room.getSenderName(), "", "", room.getSenderAvatar());
//        }

        binding.llMessageHistoryContainer.setOnClickListener(v -> {
            // Hide keyboard
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null && getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
        binding.llMessageHistoryContainer.setOnLongClickListener(v -> {
//            Toast.makeText(this, messageList.get(binding.llMessageHistoryContainer.getPosition()).getChat(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, Integer.toString(binding.llMessageHistoryContainer.getPosition()), Toast.LENGTH_SHORT).show();
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

        // Send message
        binding.btnSend.setOnClickListener(v -> {
            String messageText = binding.etMessage.getText().toString();

            DatabaseReference messageRef = XChat.database.getReference().child(XChat.refMessages).child(room.getId());
            String messageKey = messageRef.push().getKey();
            Message message = new Message(messageKey, messageText, new ArrayList<>(), MainUser.getInstance().getId(), MainUser.getInstance().getFirstName(), MainUser.getInstance().getAvatar(), room.getReceiverId(), room.getReceiverName(), room.getReceiverAvatar(), Date.from(Instant.now()));
            messageRef.child(messageKey).setValue(message).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    room.setLastChat(messageText);
                    Map<String, Object> updateRoom = new HashMap<>();
                    updateRoom.put(room.getId(), room.toRoomReference());
                    XChat.database.getReference().child(XChat.refRooms).updateChildren(updateRoom);

//                    messageListAdapter.addNewMessage(message); // Already added in onValueChangeListener
                    binding.etMessage.setText("");
                    binding.rvMessageList.scrollToPosition(0);
                } else {
                    Log.e(this.toString(), task.getException().getMessage());
                    Toast.makeText(RoomActivity.this, getResources().getString(R.string.failedAddMessage), Toast.LENGTH_LONG).show();
                }
            });
        });

//        showOldMessageList();
    }

    private void showMessageList() {
        messageListAdapter = new MessageListAdapter(this, messageList, binding.llMessageHistoryContainer);
        binding.rvMessageList.setAdapter(messageListAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);

        binding.rvMessageList.setLayoutManager(layoutManager);
        if ((binding.rvMessageList.getItemAnimator()) != null) {
            ((SimpleItemAnimator) binding.rvMessageList.getItemAnimator()).setSupportsChangeAnimations(false);
        }
        XChat.database.getReference().child(XChat.refMessages).child(room.getId()).limitToLast(XChat.messageListLimit).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    messageListAdapter.addItem(snapshot.getValue(Message.class));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showOldMessageList() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new DateLongIntFormatAdapter());
        Gson gson = gsonBuilder.create();
        Type listType = new TypeToken<ArrayList<Message>>(){}.getType();
//        messageList = gson.fromJson(room.getMessageListJson(), listType); // Sorted time_stamp descending
        messageListAdapter = new MessageListAdapter(this, messageList, binding.llMessageHistoryContainer);
        binding.rvMessageList.setAdapter(messageListAdapter);

//        rvMessageList.addItemDecoration(new DividerItemDecoration(rvMessageList.getContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);

        binding.rvMessageList.setLayoutManager(layoutManager);
        if ((binding.rvMessageList.getItemAnimator()) != null) {
            ((SimpleItemAnimator) binding.rvMessageList.getItemAnimator()).setSupportsChangeAnimations(false);
        }
    }
}