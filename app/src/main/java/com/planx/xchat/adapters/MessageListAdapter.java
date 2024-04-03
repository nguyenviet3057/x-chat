package com.planx.xchat.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.planx.xchat.R;
import com.planx.xchat.sqlite.Message;
import com.planx.xchat.entities.User;
import com.planx.xchat.enums.MessageType;
import com.planx.xchat.interfaces.IOnItemClickListener;
import com.planx.xchat.utils.Utils;

import java.util.ArrayList;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageListViewHolder>{

    private Context context;
    private ArrayList<Message> messageList;
    private IOnItemClickListener itemClickListener;

    public MessageListAdapter(Context context, ArrayList<Message> messageList, IOnItemClickListener itemClickListener) {
        this.context = context;
        this.messageList = messageList;
        this.itemClickListener = itemClickListener;
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

        if (position > 0 && messageList.get(position - 1).getSenderId() == message.getSenderId()) { // Hide avatar if previous message belong to the same user
            holder.rlMessageItem.setPadding(holder.rlMessageItem.getPaddingLeft(), holder.rlMessageItem.getPaddingTop(), holder.rlMessageItem.getPaddingRight(), 0);

            if (message.getSenderId() != User.getInstance().getId()) {
                holder.ivAvatar.setVisibility(View.INVISIBLE);
            }
        } else {
            if (position < messageList.size() - 1 && messageList.get(position + 1).getSenderId() == message.getSenderId()) { // Closer from bottom to the next message if in the same group message
                holder.rlMessageItem.setPadding(holder.rlMessageItem.getPaddingLeft(), holder.rlMessageItem.getPaddingTop(), holder.rlMessageItem.getPaddingRight(), 0);
            }
            if (message.getSenderId() != User.getInstance().getId()) { // Show avatar if sender is not main user and the current message is the first of the group message
                Glide.with(context).load(message.getSenderAvatar()).into(holder.ivAvatar);
                holder.ivAvatar.setVisibility(View.VISIBLE);
            }
        }

        if (position == 0) {
            holder.rlMessageItem.setPadding(holder.rlMessageItem.getPaddingLeft(), holder.rlMessageItem.getPaddingTop(), holder.rlMessageItem.getPaddingRight(), Utils.dp2px(holder.rlMessageItem.getResources(), 30));
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

        ConstraintLayout rlMessageItem;
        ImageView ivAvatar;
        TextView tvChat;

        public MessageListViewHolder(@NonNull View itemView) {
            super(itemView);
            rlMessageItem = itemView.findViewById(R.id.clMessageItem);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvChat = itemView.findViewById(R.id.tvChat);

            itemView.setOnClickListener(v -> {
                itemClickListener.onItemClick(getAdapterPosition());
            });
            itemView.setOnLongClickListener(v -> {
                itemClickListener.onItemLongClick(getAdapterPosition());
                return true;
            });
        }
    }
}
