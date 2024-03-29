package com.planx.xchat.utils;

import android.content.res.Resources;
import android.util.TypedValue;

public class Utils {
    public static final int dp2px(Resources r, float dp) {
        return (int) (dp * r.getDisplayMetrics().density + 0.5f);
    }
}
