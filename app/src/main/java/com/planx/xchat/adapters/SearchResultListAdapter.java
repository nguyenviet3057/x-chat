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
import com.planx.xchat.interfaces.IOnItemClickListener;
import com.planx.xchat.models.MainUser;
import com.planx.xchat.models.Room;
import com.planx.xchat.models.User;
import com.planx.xchat.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SearchResultListAdapter extends RecyclerView.Adapter<SearchResultListAdapter.RoomListViewHolder> {

    private Context context;
    private ArrayList<User> userList;
    private IOnItemClickListener itemClickListener;

    public SearchResultListAdapter(Context context, ArrayList<User> userList, IOnItemClickListener itemClickListener) {
        this.context = context;
        this.userList = userList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RoomListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_row_item, parent, false);
        return new SearchResultListAdapter.RoomListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomListViewHolder holder, int position) {
        User user = userList.get(position);
        Glide.with(context).load(user.getAvatar()).into(holder.ivAvatar);
        holder.tvUserName.setText(user.getFullName());
    }

    @Override
    public int getItemCount() {
        if (userList != null && userList.size() > 0)
            return userList.size();
        else
            return 0;
    }

    public void updateFriendList(ArrayList<User> friendList) {
        userList = friendList;
        notifyDataSetChanged();
    }

    public class RoomListViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rlMessageItem;
        ImageView ivAvatar;
        TextView tvUserName;

        public RoomListViewHolder(@NonNull View itemView) {
            super(itemView);
            rlMessageItem = itemView.findViewById(R.id.clMessageItem);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);

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
