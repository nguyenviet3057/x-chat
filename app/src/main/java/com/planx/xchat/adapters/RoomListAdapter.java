package com.planx.xchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.planx.xchat.R;
import com.planx.xchat.sqlite.Room;
import com.planx.xchat.entities.User;
import com.planx.xchat.interfaces.IOnItemClickListener;

import java.util.ArrayList;

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.RoomListViewHolder>{

    private Context context;
    private ArrayList<Room> roomList;
    private IOnItemClickListener itemClickListener;

    public RoomListAdapter(Context context, ArrayList<Room> roomList, IOnItemClickListener itemClickListener) {
        this.context = context;
        this.roomList = roomList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RoomListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_row_item, parent, false);
        return new RoomListAdapter.RoomListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomListViewHolder holder, int position) {
        Room room = roomList.get(position);

        if (room.getSenderId() == User.getInstance().getId()) {
            Glide.with(context).load(room.getReceiverAvatar()).into(holder.ivAvatar);
            holder.tvUserName.setText(room.getReceiverName());
            holder.tvLastChat.setText(context.getString(R.string.sender_main_user_alias) + ": " + room.getLastChat());
        } else {
            Glide.with(context).load(room.getSenderAvatar()).into(holder.ivAvatar);
            holder.tvUserName.setText(room.getSenderName());
            holder.tvLastChat.setText(room.getSenderName() + ": " + room.getLastChat());
        }
    }

    @Override
    public int getItemCount() {
        if (roomList != null && roomList.size() > 0)
            return roomList.size();
        else
            return 0;
    }

    public class RoomListViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rlMessageItem;
        ImageView ivAvatar;
        TextView tvUserName;
        TextView tvLastChat;
        TextView tvLastTime;

        public RoomListViewHolder(@NonNull View itemView) {
            super(itemView);
            rlMessageItem = itemView.findViewById(R.id.clMessageItem);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvLastChat = itemView.findViewById(R.id.tvLastChat);
            tvLastTime = itemView.findViewById(R.id.tvLastTime);

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
