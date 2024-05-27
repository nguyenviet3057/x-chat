package com.planx.xchat.constants;

public class Constants {
    public static final String DOC_USER_PATH_FRIENDS = "friends";
    public static final String DOC_USER_PATH_ROOMS = "rooms";
    public static final String DOC_USER_PATH_FCM_TOKEN = "fcmToken";
    public static final String REF_MESSAGE_PATH_TIMESTAMP = "timestamp/time";

    // FCM configs
    public static final String TAG = "FCM";
    public static final String FCM_URL = "https://fcm.googleapis.com/v1/projects/xchat-59115/messages:send";
    public static final String SCOPE = "https://www.googleapis.com/auth/firebase.messaging";

    // NOTIFICATION TAGs
    public static final String NOTIFY_UPDATE_ELAPSED_TIME_FOR_ROOM_ROW = "UPDATE_ELAPSED_TIME";
    public static final String NOTIFY_UPDATE_ONLINE_STATUS_FOR_FRIEND_ROW_AND_ROOM_ROW = "UPDATE_ONLINE_STATUS";
    public static final String NOTIFY_UPDATE_FIRST_ROOM_INFO_IN_ROOM_LIST_RECYCLERVIEW = "UPDATE_FIRST_ROOM";
}
