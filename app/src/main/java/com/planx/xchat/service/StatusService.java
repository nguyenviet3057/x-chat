package com.planx.xchat.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldPath;
import com.planx.xchat.XChat;
import com.planx.xchat.constants.Constants;
import com.planx.xchat.models.MainUser;

import java.util.HashMap;
import java.util.Map;

public class StatusService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (MainUser.getInstance().getId() != null) {
            // Update online status
            DatabaseReference statusOnline = XChat.database.getReference().child(XChat.refOnline).child(MainUser.getInstance().getId());
            XChat.database.getReference().child(XChat.refInfoConnected).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean connected = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                            if (connected) {
                                statusOnline.setValue(true);
                                statusOnline.onDisconnect().setValue(false);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

        return START_STICKY;
    }
}
