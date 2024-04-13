package com.planx.xchat;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class XChat extends Application {

    public static Resources resources;
    public static Context context;
    public static boolean isDevelopment = false;
    public static FirebaseFirestore firestore;
    public static FirebaseDatabase database;

    // Collections
    public static String colUsers = "users";

    // References
    public static String refMembers = "members";
    public static String refRooms = "rooms";
    public static String refMessages = "messages";

    public static int messageListLimit = 10;

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
        database.getReference().child(XChat.refRooms).keepSynced(true);
        database.getReference().child(XChat.refMembers).keepSynced(true);
        database.getReference().child(XChat.refMessages).keepSynced(true);
        if (isDevelopment) {
            firestore.useEmulator("10.0.2.2", 8082);
            database.useEmulator("10.0.2.2", 9000);
        }
    }
}
