package com.planx.xchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.planx.xchat.R;
import com.planx.xchat.XChat;
import com.planx.xchat.constants.Constants;
import com.planx.xchat.databinding.FriendRowItemBinding;
import com.planx.xchat.models.MainUser;
import com.planx.xchat.interfaces.IOnItemClickListener;
import com.planx.xchat.models.User;

import java.util.ArrayList;
import java.util.List;

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
        FriendRowItemBinding binding = FriendRowItemBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new FriendListAdapter.FriendListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            holder.binding.tvOnlineStatus.setVisibility(friendList.get(position).isOnline() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListViewHolder holder, int position) {
        User friend = friendList.get(position);
        Glide.with(context).load(friend.getAvatar()).into(holder.binding.ivFriendAvatar);
        holder.binding.tvOnlineStatus.setVisibility(friend.isOnline() ? View.VISIBLE : View.INVISIBLE);

        if (friend.getId().equals(MainUser.getInstance().getId()))
            holder.binding.tvFriendName.setText(XChat.resources.getString(R.string.sender_main_user_alias));
        else
            holder.binding.tvFriendName.setText(friend.getFirstName());
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
                notifyItemChanged(i, Constants.NOTIFY_UPDATE_ONLINE_STATUS_FOR_FRIEND_ROW_AND_ROOM_ROW);
                return;
            }
        }

        friendList.add(friend);
        notifyItemInserted(friendList.size() - 1);
    }

    class FriendListViewHolder extends RecyclerView.ViewHolder {
        private final FriendRowItemBinding binding;

        public FriendListViewHolder(@NonNull FriendRowItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                itemClickListener.onItemClick(getAdapterPosition());
            });
            binding.getRoot().setOnLongClickListener(v -> {
                itemClickListener.onItemLongClick(getAdapterPosition());
                return true;
            });
        }
    }
}
