package com.planx.xchat.extensions;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EditTextLoginSignup extends androidx.appcompat.widget.AppCompatEditText {
    public EditTextLoginSignup(@NonNull Context context) {
        super(context);
    }

    public EditTextLoginSignup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextLoginSignup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            this.setBackgroundTintList(null);
        } else {
            this.setBackgroundTintList(ColorStateList.valueOf((getResources().getColor(android.R.color.transparent))));
        }
    }
}
