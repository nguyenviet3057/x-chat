package com.planx.xchat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.planx.xchat.R;
import com.planx.xchat.XChat;
import com.planx.xchat.adapters.SearchResultListAdapter;
import com.planx.xchat.constants.Constants;
import com.planx.xchat.databinding.ActivitySearchBinding;
import com.planx.xchat.firebase.database.RoomReference;
import com.planx.xchat.interfaces.ICallback;
import com.planx.xchat.interfaces.IOnItemClickListener;
import com.planx.xchat.models.MainUser;
import com.planx.xchat.models.User;
import com.planx.xchat.retrofit.RetrofitClient;
import com.planx.xchat.retrofit.request.SearchRequest;
import com.planx.xchat.retrofit.response.SearchResponse;
import com.planx.xchat.retrofit.status.SearchResponseStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private ArrayList<User> friendList;
    private ArrayList<User> searchResultList;
    private SearchResultListAdapter searchResultListAdapter;
    private ICallback<String> callbackToRoom;
    private CompositeDisposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackToRoom = new ICallback<String>() {
            @Override
            public void onSuccess(String result) {
                Intent intent = new Intent(SearchActivity.this, RoomActivity.class);
                intent.putExtra(getResources().getString(R.string.Main2MessageRoomId), result);
                startActivity(intent);
            }

            @Override
            public void onFailure(String error) {

            }
        };

        friendList = new ArrayList<>();
        disposable = new CompositeDisposable();
        searchResultList = new ArrayList<>();

        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        searchResultListAdapter = new SearchResultListAdapter(this, searchResultList, new IOnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                retrieveRoomByFriend(searchResultList.get(position));
            }

            @Override
            public void onItemLongClick(int position) {

            }
        });
        binding.rvFriendListResult.setAdapter(searchResultListAdapter);
        binding.rvFriendListResult.setLayoutManager(new LinearLayoutManager(this));

        if (MainUser.getInstance().getFriends().size() > 0) {
            XChat.firestore.collection(XChat.colUsers).whereIn(FieldPath.documentId(), MainUser.getInstance().getFriends()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot docUser :
                            task.getResult().getDocuments()) {
                        friendList.add(docUser.toObject(User.class));
                    }
                } else {
                    Toast.makeText(this, "Can not find your friends", Toast.LENGTH_LONG).show();
                }
            });
        }

        PublishSubject<String> searchSubject = PublishSubject.create();
        Observable<SearchResponse> searchObservable = searchSubject
                .debounce(300, TimeUnit.MILLISECONDS)
                .startWith(Observable.just(""))
                .distinctUntilChanged()
                .switchMap(query -> {
                    if (query.isEmpty()) {
                        // Emit an empty SearchResults object or similar when the query is empty
                        return Observable.just(new SearchResponse(SearchResponseStatus.EMPTY_QUERY, "Empty", new ArrayList<>()));
                    } else {
                        // Make network request for non-empty query
                        return RetrofitClient.getInstance().getApiService().search(new SearchRequest(query))
                                .subscribeOn(Schedulers.io());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        disposable.add(searchObservable.subscribe(this::handleResults, this::handleError));
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchSubject.onNext(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.etSearch.requestFocus();
        binding.ibBack.setOnClickListener(v -> finish());
    }

    private void handleError(Throwable throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
        Log.e(this.toString(), throwable.getMessage());
    }

    private void handleResults(SearchResponse searchResponse) {
        switch (searchResponse.getStatus()) {
            case SearchResponseStatus.OK:
                searchResultList = searchResponse.getData();
                searchResultListAdapter.updateFriendList(searchResultList);
                Log.d("API data", searchResponse.getMessage().concat(": " + Integer.toString(searchResponse.getData().size())));
                break;
            case SearchResponseStatus.EMPTY_QUERY:
                searchResultList = friendList;
                searchResultListAdapter.updateFriendList(friendList);
                Log.d("Not call API", searchResponse.getMessage().concat(": " + Integer.toString(searchResponse.getData().size())));
                break;
        }
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
                            user.getFriends().addAll(participants.entrySet().stream()
                                    .filter(entry -> !entry.getKey().equals(user.getId()) && entry.getValue())
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList()));
                            XChat.firestore.collection(XChat.colUsers).document(docUserData.getId()).update(FieldPath.of(Constants.DOC_USER_PATH_ROOMS), user.getRooms());
                            XChat.firestore.collection(XChat.colUsers).document(docUserData.getId()).update(FieldPath.of(Constants.DOC_USER_PATH_FRIENDS), user.getFriends());
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
                                Toast.makeText(SearchActivity.this, getResources().getString(R.string.failedMakeRoom), Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                Toast.makeText(SearchActivity.this, getResources().getString(R.string.failedMakeRoom), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dispose all disposables to avoid memory leaks
        disposable.clear();
    }
}