package com.planx.xchat.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.TypedValue;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Utils {
    public static final int dp2px(Resources r, float dp) {
        return (int) (dp * r.getDisplayMetrics().density + 0.5f);
    }

    public static String readJsonFileFromAssets(Context context, String filename) {
        AssetManager assetManager = context.getAssets();
        try {
            assetManager.open(filename).close();
            String json = null;
            try {
                InputStream inputStream = assetManager.open(filename);
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();
                json = new String(buffer, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return json;
        } catch (IOException e) {
            return "";
        }
    }

    public static JSONArray loadJSONArrayFromAsset(Context context, String fileName) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(fileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            return new JSONArray(json);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
