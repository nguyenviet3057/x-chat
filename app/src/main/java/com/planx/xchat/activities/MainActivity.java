package com.planx.xchat.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.planx.xchat.animations.RoomListRecyclerViewAnimations;
import com.planx.xchat.constants.Constants;
import com.planx.xchat.contexts.SharedPreferencesManager;
import com.planx.xchat.databinding.ActivityMainBinding;
import com.planx.xchat.firebase.firestore.UserDocument;
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
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private DatabaseHandler db;
    private ActivityMainBinding binding;
    private ArrayList<Room> roomList;
    private RoomListAdapter roomListAdapter;
    private ArrayList<User> friendList;
    private FriendListAdapter friendListAdapter;
    private ICallback<String> callbackToRoom;
    private boolean isFirstLoad = true;

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

        askNotificationPermission();

        binding.svSearchResult.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });

        binding.ibLogout.setOnClickListener(v -> {
            SharedPreferencesManager.getInstance().clearData();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        friendList.add(0, MainUser.getInstance().toSQLiteUser());
        friendListAdapter = new FriendListAdapter(MainActivity.this, friendList, new IOnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this, friendList.get(position).getFirstName(), Toast.LENGTH_SHORT).show();

                if (position != 0) {
                    retrieveRoomByFriend(friendList.get(position));
                } else {
                    Toast.makeText(MainActivity.this, "Your personal page: " + MainUser.getInstance().getId(), Toast.LENGTH_SHORT).show();
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
//        binding.rvRoomList.setItemAnimator(null);
        binding.rvRoomList.setItemAnimator(new RoomListRecyclerViewAnimations());
        binding.rvRoomList.setAdapter(roomListAdapter);
        binding.rvRoomList.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        trackUserFriendsAndRooms();
        calculateElapsedTime();
    }

    private void calculateElapsedTime() {
        Handler mHandler = new Handler();

        // Create a Runnable to calculate and display elapsed time
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                for (int roomIndex = 0; roomIndex < roomList.size(); roomIndex++) {
                    roomListAdapter.notifyItemChanged(roomIndex, Constants.NOTIFY_UPDATE_ELAPSED_TIME_FOR_ROOM_ROW);
                }
                mHandler.postDelayed(this, 5000); // Run this Runnable again after 5 seconds
            }
        };

        // Start the initial calculation and scheduling
        mHandler.post(mRunnable);
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
                    UserDocument userDocument = value.toObject(UserDocument.class);

                    // Friends
                    if (value.contains(Constants.DOC_USER_PATH_FRIENDS)) {
                        MainUser.getInstance().setFriends(userDocument.getFriends());
                        SharedPreferencesManager.getInstance().setUserData();
                        if (MainUser.getInstance().getFriends().size() > 0) {
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
                                            XChat.database.getReference().child(XChat.refOnline).child(docUser.getId()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    User friend = docUser.toObject(User.class);
                                                    friend.setOnline(Boolean.TRUE.equals(snapshot.getValue(Boolean.class)));
                                                    friendListAdapter.addOrUpdate(friend);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Log.w("Fetch online status", "ERROR");
                                                }
                                            });
                                        }
                                    } else {
                                        Log.d(this.toString(), "Current data: null");
                                    }
                                }
                            });
                        }
                    }

                    // Rooms
                    ArrayList<Room> sortedRoomListForFistLoad = new ArrayList<>();
                    ArrayList<Room> noNeedTrackingRoomList = new ArrayList<>();
                    if (value.contains(Constants.DOC_USER_PATH_ROOMS)) {
                        MainUser.getInstance().setRooms(userDocument.getRooms());
                        SharedPreferencesManager.getInstance().setUserData();
                        List<String> needTrackingRoomList = MainUser.getInstance().getRooms();
                        needTrackingRoomList.removeIf(roomId -> roomListAdapter.getRoomList().stream().map(Room::getId).collect(Collectors.toList()).contains(roomId));
                        for (String roomId :
                                needTrackingRoomList) {
                            XChat.database.getReference().child(XChat.refRooms).child(roomId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    RoomReference roomReference = snapshot.getValue(RoomReference.class);
                                    Room room = roomReference.toSQLiteRoom();
                                    room.setId(roomId);
                                    if (room.getLastId() == null) {
                                        noNeedTrackingRoomList.add(room);
                                    } else {
                                        if (!isFirstLoad) {
                                            roomListAdapter.updateRoom(room);
                                        }

                                        // Room's online status
                                        XChat.database.getReference().child(XChat.refMembers).child(roomId).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                room.setOnline(false);
                                                for (DataSnapshot onlineStatus :
                                                        snapshot.getChildren()) {
                                                    if (!onlineStatus.getKey().equals(MainUser.getInstance().getId()) && Boolean.TRUE.equals(onlineStatus.getValue(Boolean.class))) {
                                                        room.setOnline(true);
                                                        break;
                                                    }
                                                }

                                                if (!isFirstLoad) {
                                                    roomListAdapter.updateOnlineStatus(room);
                                                } else {
                                                    sortedRoomListForFistLoad.add(room);
                                                    if (sortedRoomListForFistLoad.size() == MainUser.getInstance().getRooms().size() - noNeedTrackingRoomList.size()) {
                                                        sortedRoomListForFistLoad.sort((o1, o2) -> Long.compare(o2.getTimestamp().getTime(), o1.getTimestamp().getTime()));
                                                        roomList = sortedRoomListForFistLoad;
                                                        roomListAdapter.addAllForFirstLoad(sortedRoomListForFistLoad);
                                                        isFirstLoad = false;
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            // Update online status in each room
                            DatabaseReference onlineInRoom = XChat.database.getReference().child(XChat.refMembers).child(roomId).child(MainUser.getInstance().getId());
                            onlineInRoom.setValue(true);
                            onlineInRoom.onDisconnect().setValue(false);
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
                            XChat.firestore.collection(XChat.colUsers).document(docUserData.getId()).update(FieldPath.of(Constants.DOC_USER_PATH_ROOMS), user.getRooms());
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

    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    if (MainUser.getInstance().getFcmToken() == null) {
                        XChat.messaging.getToken()
                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        if (!task.isSuccessful()) {
                                            Log.w(this.toString(), "Fetching FCM registration token failed", task.getException());
                                            return;
                                        }

                                        // Get new FCM registration token
                                        String token = task.getResult();

                                        // Log and toast
//                                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                                        SharedPreferencesManager.getInstance().setFCMToken(token);
                                        if (SharedPreferencesManager.getInstance().getUserId() != null) {
                                            Map<String, Object> fcmToken = new HashMap<>();
                                            fcmToken.put(Constants.DOC_USER_PATH_FCM_TOKEN, token);
                                            XChat.firestore.collection(XChat.colUsers).document(SharedPreferencesManager.getInstance().getUserId()).update(fcmToken);
                                        }
                                    }
                                });
                    }
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}