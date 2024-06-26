package com.planx.xchat.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.messaging.RemoteMessage;
import com.planx.xchat.R;
import com.planx.xchat.XChat;
import com.planx.xchat.adapters.MessageListAdapter;
import com.planx.xchat.constants.AppLoginStatus;
import com.planx.xchat.constants.Constants;
import com.planx.xchat.contexts.SharedPreferencesManager;
import com.planx.xchat.databinding.ActivityRoomBinding;
import com.planx.xchat.interfaces.NetworkChangeListener;
import com.planx.xchat.models.MainUser;
import com.planx.xchat.firebase.database.RoomReference;
import com.planx.xchat.models.Message;
import com.planx.xchat.service.NetworkCallbackImpl;
import com.planx.xchat.sqlite.DatabaseHandler;
import com.planx.xchat.models.Room;
import com.planx.xchat.models.User;
import com.planx.xchat.utils.Utils;

import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RoomActivity extends AppCompatActivity implements NetworkChangeListener {

    private DatabaseHandler db;
    private ActivityRoomBinding binding;

    private ArrayList<Message> messageList;
    private MessageListAdapter messageListAdapter;
    private String roomId;
    private Room room;
    private boolean isAtBottom = true;
    private boolean isAllowLoadMore = true;
    private int messageListLimit = 30;
    private ConnectivityManager connectivityManager;
    private NetworkCallbackImpl networkCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        messageList = new ArrayList<>();

        binding = ActivityRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        networkCallback = new NetworkCallbackImpl(this);

        registerNetworkCallback();

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

                                    if (room.getParticipants().size() == 1) { // Only main user and their friend (two-person room)
                                        room.setTitle(room.getParticipants().get(0).getFirstName());
                                        room.setReceiverId(room.getParticipants().get(0).getId());
                                        room.setReceiverName(room.getParticipants().get(0).getFirstName());
                                        room.setReceiverAvatar(room.getParticipants().get(0).getAvatar());

                                        binding.tvTitle.setText(room.getTitle());
                                    }
                                    room.getParticipants().add(MainUser.getInstance().toSQLiteUser());
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
            Toast.makeText(this, getResources().getString(R.string.roomNotFound), Toast.LENGTH_LONG).show();
        }

        binding.llMessageHistoryContainer.setOnClickListener(v -> {
            // Hide keyboard
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null && getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
        binding.llMessageHistoryContainer.setOnLongClickListener(v -> {
            Toast.makeText(this, messageList.get(binding.llMessageHistoryContainer.getPosition()).getChat(), Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, Integer.toString(binding.rvMessageList.getChildAt(binding.llMessageHistoryContainer.getPosition()).getPaddingBottom()), Toast.LENGTH_SHORT).show();
            return true;
        });

        if (SharedPreferencesManager.getInstance().getLoginStatus() != AppLoginStatus.LOGIN_SUCCESS) {
            if (SharedPreferencesManager.getInstance().getLoginStatus() == AppLoginStatus.NETWORK_DISCONNECTED) {
                binding.tvAlert.setText(getString(R.string.networkDisconnectedLimitFunction));
            } else {
                binding.tvAlert.setText(getString(R.string.notLoggedInLimitFunctions));
            }
            binding.tvAlert.setVisibility(View.VISIBLE);

            binding.etMessage.setFocusable(false);
            binding.etMessage.setOnClickListener(v -> {
                Toast.makeText(this, getString(R.string.limitedFunction), Toast.LENGTH_LONG).show();
            });

            binding.btnSend.setFocusable(false);
            binding.btnSend.setOnClickListener(v -> {
                Toast.makeText(this, getString(R.string.limitedFunction), Toast.LENGTH_LONG).show();
            });
        } else {
            binding.tvAlert.setVisibility(View.GONE);

            // Send message
            binding.btnSend.setOnClickListener(v -> {
                if (!binding.etMessage.getText().toString().trim().isEmpty()) {
                    String messageText = binding.etMessage.getText().toString().trim();

                    DatabaseReference messageRef = XChat.database.getReference().child(XChat.refMessages).child(room.getId());
                    String messageKey = messageRef.push().getKey();
                    Message message = new Message(messageKey, messageText, new ArrayList<>(), MainUser.getInstance().getId(), MainUser.getInstance().getFirstName(), MainUser.getInstance().getAvatar(), room.getReceiverId(), room.getReceiverName(), room.getReceiverAvatar(), Date.from(Instant.now()));
                    messageRef.child(messageKey).setValue(message).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            room.setLastChat(messageText);
                            room.setTimestamp(Date.from(Instant.now()));
                            Map<String, Object> updateRoom = new HashMap<>();
                            updateRoom.put(room.getId(), room.toRoomReference());
                            XChat.database.getReference().child(XChat.refRooms).updateChildren(updateRoom);

                            binding.etMessage.setText("");
                            binding.rvMessageList.scrollToPosition(messageList.size() - 1);

                            String receiverFCMToken = room.getParticipants()
                                    .stream()
                                    .filter(u -> !u.getId().equals(MainUser.getInstance().getId()))
                                    .collect(Collectors.toList())
                                    .get(0)
                                    .getFcmToken();
                            if (receiverFCMToken != null) {
                                Utils.sendMessageToToken(
                                        this,
                                        receiverFCMToken,
                                        getResources().getString(R.string.app_name),
                                        MainUser.getInstance().getFirstName() + ": " + messageText,
                                        roomId
                                );
                            }
                        } else {
                            Log.e(this.toString(), task.getException().getMessage());
                            Toast.makeText(RoomActivity.this, getResources().getString(R.string.failedAddMessage), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }

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

        binding.ibBack.setOnClickListener(v -> onBackPressed());

        binding.rvMessageList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalHeight = recyclerView.computeVerticalScrollRange();
                int scrolledY = recyclerView.computeVerticalScrollOffset();
                int distanceFromBottom = totalHeight - scrolledY - recyclerView.getHeight();

                if (distanceFromBottom > Utils.dp2px(50)) {
                    isAtBottom = false;
                } else {
                    isAtBottom = true;
                }

                if (scrolledY < Utils.dp2px(50) && isAllowLoadMore && messageList.size() >= messageListLimit) {
                    isAllowLoadMore = false;
                     if (messageList.get(0).getId() == null) {
                        loadMoreMessage();
                    }
                }
            }
        });
    }

    private void showMessageList() {
        messageListAdapter = new MessageListAdapter(this, messageList, binding.llMessageHistoryContainer);
        binding.rvMessageList.setAdapter(messageListAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        binding.rvMessageList.setLayoutManager(layoutManager);
        if ((binding.rvMessageList.getItemAnimator()) != null) {
            ((SimpleItemAnimator) binding.rvMessageList.getItemAnimator()).setSupportsChangeAnimations(false);
        }

        XChat.database.getReference().child(XChat.refMessages).child(room.getId()).limitToLast(messageListLimit).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == messageListLimit) {
                    messageListAdapter.addLoadingMoreSection();
                }

                XChat.database.getReference().child(XChat.refMessages).child(room.getId()).limitToLast(messageListLimit).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot messageSnapshot, @Nullable String previousChildName) {
                        messageList.add(messageSnapshot.getValue(Message.class));
                        messageListAdapter.notifyItemInserted(messageList.size() - 1);
                        messageListAdapter.notifyItemChanged(messageList.size() - 2);

                        if (isAtBottom)
                            binding.rvMessageList.scrollToPosition(messageList.size() - 1);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot messageSnapshot, @Nullable String previousChildName) {
                        Toast.makeText(RoomActivity.this, "Latest: " + messageSnapshot.getValue(Message.class).getChat(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot messageSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot messageSnapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                messageListLimit = 10;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadMoreMessage() {
        ArrayList<Message> moreMessageList = new ArrayList<>();
        Message currentOldestMessage = messageList.get(1);

        XChat.database.getReference()
                .child(XChat.refMessages)
                .child(room.getId())
                .orderByChild(Constants.REF_MESSAGE_PATH_TIMESTAMP)
                .endBefore(currentOldestMessage.getTimestamp().getTime())
                .limitToLast(messageListLimit)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                            for (DataSnapshot messageData :
                                    snapshot.getChildren()) {
                                moreMessageList.add(messageData.getValue(Message.class));
                            }

                            if (moreMessageList.size() > 0) { // Still catch onDataChange but snapshot.getChildrenCount() equals 0
//                              Collections.reverse(moreMessageList);
                                messageList.addAll(1, moreMessageList);
                                messageListAdapter.notifyItemRangeInserted(1, moreMessageList.size());

                                isAllowLoadMore = true;

                                if (moreMessageList.size() < messageListLimit) {
                                    messageListAdapter.removeLoadingMoreSection();
                                }
                            } else {
                                messageListAdapter.removeLoadingMoreSection();
                            }

                            XChat.database.getReference()
                                    .child(XChat.refMessages)
                                    .child(room.getId())
                                    .orderByChild(Constants.REF_MESSAGE_PATH_TIMESTAMP)
                                    .endBefore(currentOldestMessage.getTimestamp().getTime())
                                    .limitToLast(messageListLimit)
                                    .addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(@NonNull DataSnapshot messageSnapshot, @Nullable String previousChildName) {
                                        }

                                        @Override
                                        public void onChildChanged(@NonNull DataSnapshot messageSnapshot, @Nullable String previousChildName) {
                                            Toast.makeText(RoomActivity.this, "Old: " + messageSnapshot.getValue(Message.class).getChat(), Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onChildRemoved(@NonNull DataSnapshot messageSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(@NonNull DataSnapshot messageSnapshot, @Nullable String previousChildName) {

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        } else {
                            messageListAdapter.removeLoadingMoreSection();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        if (!isConnected) {
            runOnUiThread(() -> {
                binding.tvAlert.setText(getString(R.string.networkDisconnectedLimitFunction));
                binding.tvAlert.setVisibility(View.VISIBLE);
                SharedPreferencesManager.getInstance().setLoginStatus(AppLoginStatus.NETWORK_DISCONNECTED);

                binding.etMessage.setFocusable(false);
                binding.etMessage.setOnClickListener(v -> {
                    Toast.makeText(this, getString(R.string.limitedFunction), Toast.LENGTH_LONG).show();
                });

                binding.btnSend.setFocusable(false);
                binding.btnSend.setOnClickListener(v -> {
                    Toast.makeText(this, getString(R.string.limitedFunction), Toast.LENGTH_LONG).show();
                });
            });
        } else {
            runOnUiThread(() -> {
                binding.tvAlert.setVisibility(View.GONE);
                SharedPreferencesManager.getInstance().setLoginStatus(AppLoginStatus.LOGIN_SUCCESS);

                binding.etMessage.setFocusable(true);
                binding.btnSend.setFocusable(true);

                // Send message
                binding.btnSend.setOnClickListener(v -> {
                    if (!binding.etMessage.getText().toString().trim().isEmpty()) {
                        String messageText = binding.etMessage.getText().toString().trim();

                        DatabaseReference messageRef = XChat.database.getReference().child(XChat.refMessages).child(room.getId());
                        String messageKey = messageRef.push().getKey();
                        Message message = new Message(messageKey, messageText, new ArrayList<>(), MainUser.getInstance().getId(), MainUser.getInstance().getFirstName(), MainUser.getInstance().getAvatar(), room.getReceiverId(), room.getReceiverName(), room.getReceiverAvatar(), Date.from(Instant.now()));
                        messageRef.child(messageKey).setValue(message).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                room.setLastChat(messageText);
                                room.setTimestamp(Date.from(Instant.now()));
                                Map<String, Object> updateRoom = new HashMap<>();
                                updateRoom.put(room.getId(), room.toRoomReference());
                                XChat.database.getReference().child(XChat.refRooms).updateChildren(updateRoom);

                                binding.etMessage.setText("");
                                binding.rvMessageList.scrollToPosition(messageList.size() - 1);

                                String receiverFCMToken = room.getParticipants()
                                        .stream()
                                        .filter(u -> !u.getId().equals(MainUser.getInstance().getId()))
                                        .collect(Collectors.toList())
                                        .get(0)
                                        .getFcmToken();
                                if (receiverFCMToken != null) {
                                    Utils.sendMessageToToken(
                                            this,
                                            receiverFCMToken,
                                            getResources().getString(R.string.app_name),
                                            MainUser.getInstance().getFirstName() + ": " + messageText,
                                            roomId
                                    );
                                }
                            } else {
                                Log.e(this.toString(), task.getException().getMessage());
                                Toast.makeText(RoomActivity.this, getResources().getString(R.string.failedAddMessage), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkCallback();
    }

    private void registerNetworkCallback() {
        connectivityManager.registerDefaultNetworkCallback(networkCallback);
    }

    private void unregisterNetworkCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }
}