package com.planx.xchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.planx.xchat.R;
import com.planx.xchat.XChat;
import com.planx.xchat.models.MainUser;
import com.planx.xchat.interfaces.IOnItemClickListener;
import com.planx.xchat.models.User;

import java.util.ArrayList;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendListViewHolder> {

    private Context context;
    private ArrayList<User> friendList;
    private IOnItemClickListener itemClickListener;

    public FriendListAdapter(Context context, ArrayList<User> friendList, IOnItemClickListener itemClickListener) {
        this.context = context;
        this.friendList = friendList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public FriendListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_row_item, parent, false);
        return new FriendListAdapter.FriendListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListViewHolder holder, int position) {
        User friend = friendList.get(position);
        Glide.with(context).load(friend.getAvatar()).into(holder.ivFriendAvatar);

        if (friend.getId() == MainUser.getInstance().getId())
            holder.tvFriendName.setText(XChat.resources.getString(R.string.sender_main_user_alias));
        else
            holder.tvFriendName.setText(friend.getFirstName());
    }

    @Override
    public int getItemCount() {
        if (friendList != null && friendList.size() > 0)
            return friendList.size();
        else
            return 0;
    }

    public void addOrUpdate(User friend) {
        for (int i = 0; i < friendList.size(); i++) {
            if (friendList.get(i).getId().equals(friend.getId())) {
                friendList.set(i, friend);
                notifyItemChanged(i, 1);
                return;
            }
        }

        friendList.add(friend);
        notifyItemInserted(friendList.size() - 1);
    }

    class FriendListViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivFriendAvatar;
        private TextView tvFriendName;

        public FriendListViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFriendAvatar = itemView.findViewById(R.id.ivFriendAvatar);
            tvFriendName = itemView.findViewById(R.id.tvFriendName);

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
