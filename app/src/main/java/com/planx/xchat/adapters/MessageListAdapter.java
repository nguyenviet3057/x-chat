package com.planx.xchat.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.planx.xchat.R;
import com.planx.xchat.entities.Message;
import com.planx.xchat.entities.User;
import com.planx.xchat.enums.MessageType;

import java.util.ArrayList;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageListViewHolder>{

    private Context context;
    private ArrayList<Message> messageList;

    public MessageListAdapter(Context context, ArrayList<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MessageType.OWNER.ordinal()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_group_right, parent, false);
            return new MessageListViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_group_left, parent, false);
            return new MessageListViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (User.getInstance() != null && messageList.get(position).getSenderId() == User.getInstance().getId()) {
            return MessageType.OWNER.ordinal();
        }
        return MessageType.OTHER.ordinal();
    }

    @Override
    public void onBindViewHolder(@NonNull MessageListViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.tvChat.setText(message.getChat());

        if (position > 0 && messageList.get(position - 1).getSenderId() == message.getSenderId()) {
            holder.ivAvatar.setVisibility(View.INVISIBLE);
        } else {
            Glide.with(context).load(message.getSenderAvatar()).into(holder.ivAvatar);
        }
    }

    @Override
    public int getItemCount() {
        if (messageList != null && messageList.size() != 0)
            return messageList.size();
        else
            return 0;
    }

    public class MessageListViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAvatar;
        TextView tvChat;

        public MessageListViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvChat = itemView.findViewById(R.id.tvChat);
        }
    }
}
