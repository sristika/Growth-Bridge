package com.example.pro_hive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GroupUserAdapter extends RecyclerView.Adapter<GroupUserAdapter.GroupUserViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private List<String> mSelectedUserIds;

    public GroupUserAdapter(Context context, List<User> users) {
        mContext = context;
        mUsers = users;
        mSelectedUserIds = new ArrayList<>();
    }

    @NonNull
    @Override
    public GroupUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_chat_user_item_row, parent, false);
        return new GroupUserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupUserViewHolder holder, int position) {
        User user = mUsers.get(position);
        String name = user.getFirstName()+" "+user.getLastName();
        if(user.isVc()){
            name+=" {VC}";
        }
        holder.mUserNameTextView.setText(name);
        holder.mUserCheckBox.setChecked(mSelectedUserIds.contains(user.getId()));
        holder.mUserCheckBox.setOnClickListener(v -> {
            boolean isChecked = holder.mUserCheckBox.isChecked();
            if (isChecked) {
                mSelectedUserIds.add(user.getId());
            } else {
                mSelectedUserIds.remove(user.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public List<String> getSelectedUserIds() {
        return mSelectedUserIds;
    }

    public static class GroupUserViewHolder extends RecyclerView.ViewHolder {
        public CheckBox mUserCheckBox;
        public TextView mUserNameTextView;

        public GroupUserViewHolder(View itemView) {
            super(itemView);
            mUserCheckBox = itemView.findViewById(R.id.user_checkbox);
            mUserNameTextView = itemView.findViewById(R.id.user_name_textview);
        }
    }
}

