package com.planx.xchat.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.planx.xchat.R;
import com.planx.xchat.XChat;
import com.planx.xchat.adapters.FriendListAdapter;
import com.planx.xchat.adapters.RoomListAdapter;
import com.planx.xchat.constants.Constants;
import com.planx.xchat.contexts.SharedPreferencesManager;
import com.planx.xchat.databinding.ActivityMainBinding;
import com.planx.xchat.interfaces.ICallback;
import com.planx.xchat.models.MainUser;
import com.planx.xchat.firebase.database.RoomReference;
import com.planx.xchat.interfaces.IOnItemClickListener;
import com.planx.xchat.models.Room;
import com.planx.xchat.sqlite.DatabaseHandler;
import com.planx.xchat.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DatabaseHandler db;
    private ActivityMainBinding binding;
    private ArrayList<Room> roomList;
    private RoomListAdapter roomListAdapter;
    private ArrayList<User> friendList;
    private FriendListAdapter friendListAdapter;
    private ICallback<String> callbackToRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        friendList = new ArrayList<>();
        roomList = new ArrayList<>();
        callbackToRoom = new ICallback<String>() {
            @Override
            public void onSuccess(String result) {
                Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                intent.putExtra(getResources().getString(R.string.Main2MessageRoomId), result);
                startActivity(intent);
            }

            @Override
            public void onFailure(String error) {

            }
        };

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        friendList.add(0, MainUser.getInstance().toSQLiteUser());
        friendListAdapter = new FriendListAdapter(MainActivity.this, friendList, new IOnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this, friendList.get(position).getFirstName(), Toast.LENGTH_SHORT).show();

                if (position != 0) {
                    retrieveRoomByFriend(friendList.get(position));
                } else {
                    Toast.makeText(MainActivity.this, "Your personal page", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onItemLongClick(int position) {

            }
        });
        binding.rvFriendList.setAdapter(friendListAdapter);
        binding.rvFriendList.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));

        roomListAdapter = new RoomListAdapter(MainActivity.this, roomList, new IOnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                callbackToRoom.onSuccess(roomList.get(position).getId());
            }

            @Override
            public void onItemLongClick(int position) {

            }
        });
        binding.rvRoomList.setAdapter(roomListAdapter);
        binding.rvRoomList.setLayoutManager(new LinearLayoutManager(MainActivity.this));

//        retrieveFriendList();
        trackUserFriendsAndRooms();
    }

    private void trackUserFriendsAndRooms() {
        XChat.firestore.collection(XChat.colUsers).document(MainUser.getInstance().getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(this.toString(), "Listen failed.");
                    return;
                }
                if (value != null && value.exists() && value.toObject(MainUser.class) != null) {
                    if (value.contains(Constants.docUserPathFriends)) {
                        MainUser.getInstance().setFriends(value.toObject(MainUser.class).getFriends());
                        SharedPreferencesManager.getInstance().setUserData();
                        XChat.firestore.collection(XChat.colUsers).whereIn(FieldPath.documentId(), MainUser.getInstance().getFriends()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    Log.w(this.toString(), "Listen failed.");
                                    return;
                                }
                                if (value != null) {
                                    for (DocumentSnapshot docUser :
                                            value.getDocuments()) {
                                        friendListAdapter.addOrUpdate(docUser.toObject(User.class));
                                    }
                                } else {
                                    Log.d(this.toString(), "Current data: null");
                                }
                            }
                        });
                    }

                    if (value.contains(Constants.docUserPathRooms)) {
                        MainUser.getInstance().setRooms(value.toObject(MainUser.class).getRooms());
                        SharedPreferencesManager.getInstance().setUserData();
                        List<String> roomIds = MainUser.getInstance().getRooms();
                        for (String roomId :
                                roomIds) {
                            XChat.database.getReference().child(XChat.refRooms).child(roomId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        RoomReference roomReference = snapshot.getValue(RoomReference.class);
                                        Room room = roomReference.toSQLiteRoom();
                                        room.setId(roomId);
                                        if (room.getLastId() != null)
                                            roomListAdapter.addOrUpdate(room);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                } else {
                    Log.d(this.toString(), "Current data: null");
                }
            }
        });
    }

    private void retrieveRoomByFriend(User friend) {
        List<User> participantList = new ArrayList<>();
        participantList.add(MainUser.getInstance().toSQLiteUser());
        participantList.add(friend);

        XChat.database.getReference().child(XChat.refMembers).orderByChild(MainUser.getInstance().getId()).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check for existed room with current friend
                for (DataSnapshot dataMember :
                        snapshot.getChildren()) {
                    if (dataMember.child(friend.getId()).exists() && dataMember.getChildrenCount() == 2) {
                        callbackToRoom.onSuccess(dataMember.getKey());
                        return;
                    }
                }

                // No existed room with current friend
                makeNewRoom(participantList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void makeNewRoom(List<User> participantList) {
        // Parse to Map participants for room reference
        Map<String, Boolean> participants = new HashMap<>();
        for (User participant :
                participantList) {
            participants.put(participant.getId(), true);
        }

        DatabaseReference refRooms = XChat.database.getReference().child(XChat.refRooms);
        String roomKey = refRooms.push().getKey();

        RoomReference roomReference = new RoomReference();
        roomReference.setParticipants(participants);

        refRooms.child(roomKey).setValue(roomReference).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                XChat.firestore.collection(XChat.colUsers).whereIn(FieldPath.documentId(), new ArrayList<>(roomReference.getParticipants().keySet())).get().addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        for (DocumentSnapshot docUserData :
                                updateTask.getResult().getDocuments()) {
                            User user = docUserData.toObject(User.class);
                            user.getRooms().add(roomKey);
                            XChat.firestore.collection(XChat.colUsers).document(docUserData.getId()).update(FieldPath.of(Constants.docUserPathRooms), user.getRooms());
                        }
                    }
                });
                XChat.database.getReference().child(XChat.refMembers)
                        .child(roomKey)
                        .setValue(participants)
                        .addOnCompleteListener(subtask -> {
                            if (subtask.isSuccessful()) {
                                callbackToRoom.onSuccess(roomKey);
                            } else {
                                Log.e(this.toString(), subtask.getException().getMessage());
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.failedMakeRoom), Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.failedMakeRoom), Toast.LENGTH_LONG).show();
            }
        });
    }
}