package com.planx.xchat.adapters;

import android.content.Context;
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
import com.planx.xchat.models.Message;
import com.planx.xchat.models.MainUser;
import com.planx.xchat.enums.MessageType;
import com.planx.xchat.interfaces.IOnItemClickListener;
import com.planx.xchat.utils.Utils;

import java.util.ArrayList;
import java.util.List;

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
        if (viewType == MessageType.LOADMORE.ordinal()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more_message, parent, false);
            return new MessageListViewHolder(view);
        } else if (viewType == MessageType.OWNER.ordinal()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_group_right, parent, false);
            return new MessageListViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_group_left, parent, false);
            return new MessageListViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && messageList.get(0).getId() == null) return MessageType.LOADMORE.ordinal();
        if (MainUser.getInstance() != null && messageList.get(position).getSenderId().equals(MainUser.getInstance().getId())) {
            return MessageType.OWNER.ordinal();
        }
        return MessageType.OTHER.ordinal();
    }

    @Override
    public void onBindViewHolder(@NonNull MessageListViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (message.getId() != null && position == messageList.size() - 1) {
            holder.rlMessageItem.setPadding(holder.rlMessageItem.getPaddingLeft(), holder.rlMessageItem.getPaddingTop(), holder.rlMessageItem.getPaddingRight(), Utils.dp2px(20));
        }

        if (position == 0) {
            if (message.getId() == null) {

            } else {
                holder.tvChat.setText(message.getChat());
                if (messageList.size() == 1 && !message.getSenderId().equals(MainUser.getInstance().getId())) {
                    Glide.with(context).load(message.getSenderAvatar()).into(holder.ivAvatar);
                    holder.ivAvatar.setVisibility(View.VISIBLE);
                } else {
                    if (messageList.get(position + 1).getSenderId().equals(message.getSenderId())
                            && !message.getSenderId().equals(MainUser.getInstance().getId())) {
                        holder.ivAvatar.setVisibility(View.INVISIBLE);
                        holder.rlMessageItem.setPadding(holder.rlMessageItem.getPaddingLeft(), holder.rlMessageItem.getPaddingTop(), holder.rlMessageItem.getPaddingRight(), 0);
                    } else if (!messageList.get(position + 1).getSenderId().equals(message.getSenderId())
                            && !message.getSenderId().equals(MainUser.getInstance().getId())) {
                        Glide.with(context).load(message.getSenderAvatar()).into(holder.ivAvatar);
                        holder.ivAvatar.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else {
            holder.tvChat.setText(message.getChat());

            if (position == messageList.size() - 1) {
                if (!message.getSenderId().equals(MainUser.getInstance().getId())) {
                    Glide.with(context).load(message.getSenderAvatar()).into(holder.ivAvatar);
                    holder.ivAvatar.setVisibility(View.VISIBLE);
                }
            } else if (position == 1) {
                if (messageList.get(position + 1).getSenderId().equals(message.getSenderId())) {
                    if (!message.getSenderId().equals(MainUser.getInstance().getId())) {
                        holder.ivAvatar.setVisibility(View.INVISIBLE);
                    }
                    holder.rlMessageItem.setPadding(holder.rlMessageItem.getPaddingLeft(), holder.rlMessageItem.getPaddingTop(), holder.rlMessageItem.getPaddingRight(), 0);
                } else {
                    if (!message.getSenderId().equals(MainUser.getInstance().getId())) {
                        Glide.with(context).load(message.getSenderAvatar()).into(holder.ivAvatar);
                        holder.ivAvatar.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                if (!messageList.get(position - 1).getSenderId().equals(message.getSenderId())
                        && messageList.get(position + 1).getSenderId().equals(message.getSenderId())) {
                    if (!message.getSenderId().equals(MainUser.getInstance().getId())) {
                        holder.ivAvatar.setVisibility(View.INVISIBLE);
                    }
                    holder.rlMessageItem.setPadding(holder.rlMessageItem.getPaddingLeft(), holder.rlMessageItem.getPaddingTop(), holder.rlMessageItem.getPaddingRight(), 0);
                } else if (messageList.get(position - 1).getSenderId().equals(message.getSenderId())
                        && messageList.get(position + 1).getSenderId().equals(message.getSenderId())) {
                    if (!message.getSenderId().equals(MainUser.getInstance().getId())) {
                        holder.ivAvatar.setVisibility(View.INVISIBLE);
                    }
                    holder.rlMessageItem.setPadding(holder.rlMessageItem.getPaddingLeft(), holder.rlMessageItem.getPaddingTop(), holder.rlMessageItem.getPaddingRight(), 0);
                } else if (messageList.get(position - 1).getSenderId().equals(message.getSenderId())
                        && !messageList.get(position + 1).getSenderId().equals(message.getSenderId())) {
                    if (!message.getSenderId().equals(MainUser.getInstance().getId())) {
                        Glide.with(context).load(message.getSenderAvatar()).into(holder.ivAvatar);
                        holder.ivAvatar.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (messageList != null && messageList.size() != 0)
            return messageList.size();
        else
            return 0;
    }

    public void addLoadingMoreSection() {
        messageList.add(0, new Message());

        notifyItemChanged(0, 1);

        notifyItemInserted(0);
    }
    public void removeLoadingMoreSection() {
        messageList.remove(0);
        notifyItemRemoved(0);
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
