package com.planx.xchat.extensions;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.planx.xchat.interfaces.IOnItemClickListener;

public class LinearLayoutIOnItemClickListener extends LinearLayout implements IOnItemClickListener {

    private int position;

    public LinearLayoutIOnItemClickListener(Context context) {
        super(context);
    }

    public LinearLayoutIOnItemClickListener(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayoutIOnItemClickListener(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LinearLayoutIOnItemClickListener(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onItemClick(int position) {
        this.performClick();
    }

    @Override
    public void onItemLongClick(int position) {
        this.position = position;
        this.performLongClick();
    }

    public int getPosition() {
        return position;
    }
}
