package com.planx.xchat;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.messaging.FirebaseMessaging;
import com.planx.xchat.contexts.SharedPreferencesManager;

import java.util.Set;

public class XChat extends Application {

    public static Resources resources;
    public static Context context;
    public static boolean isDevelopment = false;
    public static FirebaseFirestore firestore;
    public static FirebaseDatabase database;
    public static FirebaseMessaging messaging;

    // Collections
    public static String colUsers = "users";

    // References
    public static String refOnline = "online";
    public static String refMembers = "members";
    public static String refRooms = "rooms";
    public static String refMessages = "messages";
    public static String refInfoConnected = ".info/connected";

    @Override
    public void onCreate() {
        super.onCreate();
        resources = getResources();
        context = getApplicationContext();

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        firestore = FirebaseFirestore.getInstance();
        firestore.setFirestoreSettings(
                new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
        );
        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        database.getReference().child(XChat.refOnline).keepSynced(true);
        database.getReference().child(XChat.refRooms).keepSynced(true);
        database.getReference().child(XChat.refMembers).keepSynced(true);
        database.getReference().child(XChat.refMessages).keepSynced(true);
        if (isDevelopment) {
            firestore.useEmulator("10.0.2.2", 8082);
            database.useEmulator("10.0.2.2", 9000);
        }

        messaging = FirebaseMessaging.getInstance();
    }
}
