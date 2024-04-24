package com.planx.xchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.planx.xchat.R;
import com.planx.xchat.constants.Constants;
import com.planx.xchat.databinding.RoomRowItemBinding;
import com.planx.xchat.models.Room;
import com.planx.xchat.models.MainUser;
import com.planx.xchat.interfaces.IOnItemClickListener;
import com.planx.xchat.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        RoomRowItemBinding binding = RoomRowItemBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new RoomListAdapter.RoomListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomListViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            for (Object payload :
                    payloads) {
                switch ((String) payload) {
                    case Constants.NOTIFY_UPDATE_ELAPSED_TIME_FOR_ROOM_ROW:
                        holder.binding.tvLastTime.setText(Utils.formatLastTime(roomList.get(position).getTimestamp().getTime()));
                        break;
                    case Constants.NOTIFY_UPDATE_ONLINE_STATUS_FOR_FRIEND_ROW_AND_ROOM_ROW:
                        holder.binding.tvOnlineStatus.setVisibility(roomList.get(position).isOnline() ? View.VISIBLE : View.INVISIBLE);
                        break;
                    case Constants.NOTIFY_UPDATE_FIRST_ROOM_INFO_IN_ROOM_LIST_RECYCLERVIEW:
                        Room room = roomList.get(position);
                        if (room.getLastId().equals(MainUser.getInstance().getId())) {
                            holder.binding.tvLastChat.setText(context.getString(R.string.sender_main_user_alias) + ": " + room.getLastChat());
                        } else {
                            holder.binding.tvLastChat.setText(room.getSenderName() + ": " + room.getLastChat());
                        }
                        holder.binding.tvLastTime.setText(Utils.formatLastTime(room.getTimestamp().getTime()));
                        break;
                }
            };
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RoomListViewHolder holder, int position) {
        Room room = roomList.get(position);

        if (room.getSenderId().equals(MainUser.getInstance().getId())) {
            Glide.with(context).load(room.getReceiverAvatar()).into(holder.binding.ivAvatar);
            holder.binding.tvUserName.setText(room.getReceiverName());
            holder.binding.tvLastChat.setText(context.getString(R.string.sender_main_user_alias) + ": " + room.getLastChat());
        } else {
            Glide.with(context).load(room.getSenderAvatar()).into(holder.binding.ivAvatar);
            holder.binding.tvUserName.setText(room.getSenderName());
            holder.binding.tvLastChat.setText(room.getSenderName() + ": " + room.getLastChat());
        }

        holder.binding.tvLastTime.setText(Utils.formatLastTime(room.getTimestamp().getTime()));
        holder.binding.tvOnlineStatus.setVisibility(room.isOnline() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        if (roomList != null && roomList.size() > 0)
            return roomList.size();
        else
            return 0;
    }

    public ArrayList<Room> getRoomList() {
        return roomList;
    }

    public void setRoomList(ArrayList<Room> roomList) {
        this.roomList = roomList;
    }

    public void addAllForFirstLoad(ArrayList<Room> sortedRoomListForFistLoad) {
        this.roomList = sortedRoomListForFistLoad;
        notifyItemRangeInserted(0, sortedRoomListForFistLoad.size());
    }

    public void updateRoom(Room room) {
        Optional<Room> roomTarget = roomList.stream().filter(obj -> obj.getId().equals(room.getId())).findFirst();
        if (roomTarget.isPresent()) {
            int roomIndex = roomList.indexOf(roomTarget.get());
            if (roomIndex == 0) {
                roomList.set(roomIndex, room);
                notifyItemChanged(roomIndex, Constants.NOTIFY_UPDATE_FIRST_ROOM_INFO_IN_ROOM_LIST_RECYCLERVIEW);
                return;
            } else {
                roomList.remove(roomIndex);
                notifyItemRemoved(roomIndex);
            }
        }

        roomList.add(0, room);
        notifyItemInserted(0);
    }

    public void updateOnlineStatus(Room room) {
        Optional<Room> roomTarget = roomList.stream().filter(obj -> obj.getId().equals(room.getId())).findFirst();
        if (roomTarget.isPresent()) {
            notifyItemChanged(roomList.indexOf(roomTarget.get()), Constants.NOTIFY_UPDATE_ONLINE_STATUS_FOR_FRIEND_ROW_AND_ROOM_ROW);
        }
    }

    public class RoomListViewHolder extends RecyclerView.ViewHolder {

        private final RoomRowItemBinding binding;

        public RoomListViewHolder(@NonNull RoomRowItemBinding binding) {
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
