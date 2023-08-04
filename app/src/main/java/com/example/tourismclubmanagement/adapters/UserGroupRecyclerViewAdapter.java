package com.example.tourismclubmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourismclubmanagement.R;
import com.example.tourismclubmanagement.models.ChatMessage;
import com.example.tourismclubmanagement.models.Group;
import com.example.tourismclubmanagement.models.GroupInfo;
import com.example.tourismclubmanagement.models.User;
import com.example.tourismclubmanagement.models.UserGroup;
import com.example.tourismclubmanagement.models.UserInGroupInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UserGroupRecyclerViewAdapter extends RecyclerView.Adapter<UserGroupRecyclerViewAdapter.ViewHolder> {

    private List<Group> groupsList;
    private User user;
    private OnItemClickListener mListener;
    private DatabaseReference usersDatasource;
    public interface OnItemClickListener {
        void onItemClick(String groupId);

    }
    public void setOnItemClickListener(UserGroupRecyclerViewAdapter.OnItemClickListener listener) {
        mListener = listener;
    }
    public UserGroupRecyclerViewAdapter(List<Group> groupsList,User user) {
        this.groupsList = groupsList;
        this.user = user;
    }

    @NonNull
    @Override
    public UserGroupRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_groups_list_item, parent, false);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://tourismclubmanagement-default-rtdb.europe-west1.firebasedatabase.app/");
        usersDatasource = database.getReference("users");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserGroupRecyclerViewAdapter.ViewHolder holder, int position) {
        Group group = groupsList.get(position);
        GroupInfo groupInfo = group.getGroupInfo();
        holder.groupNameField.setText( groupInfo.getGroupName());
        UserGroup tempGroup = new UserGroup();
        List<UserGroup> userGroups = user.getGroups();
        for (UserGroup userGroup:userGroups) {
            if (userGroup.getGroupId().equals(groupInfo.getId())){
                tempGroup = userGroup;
                if (userGroup.getFavourite()){
                    holder.favouriteButton.setBackgroundResource(R.drawable.favourite_star_active);
                }
                else {
                    holder.favouriteButton.setBackgroundResource(R.drawable.favourite_star_inactive);
                }
                break;
            }
        }
        UserGroup finalTempGroup = tempGroup;
        holder.favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalTempGroup.getFavourite()){
                    finalTempGroup.setFavourite(false);
                    usersDatasource.child(user.getUserInfo().getId()).child("groups").child(groupInfo.getId()).child("favourite").setValue(false);
                }
                else {
                    finalTempGroup.setFavourite(true);
                    usersDatasource.child(user.getUserInfo().getId()).child("groups").child(groupInfo.getId()).child("favourite").setValue(true);
                }

            }
        });
        holder.id = groupInfo.getId();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(holder.id);
                }
            }
        });
        for (UserInGroupInfo userInGroup : group.getUsersInGroup()) {
            if (userInGroup.getId().equals(user.getUserInfo().getId())){
                List<ChatMessage> chatMessages = group.getChat();
                if (!chatMessages.isEmpty()){
                    if (userInGroup.getLastLogin().before(chatMessages.get(chatMessages.size()-1).getDate())){
                        holder.itemView.setBackgroundResource(R.drawable.admin_item_background);
                    }
                    else {
                        holder.itemView.setBackgroundResource(R.drawable.groups_item_background);
                    }
                }
                else {
                    holder.itemView.setBackgroundResource(R.drawable.groups_item_background);
                }
                break;
            }

        }
    }

    @Override
    public int getItemCount() {
        return groupsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       TextView groupNameField;
       AppCompatButton favouriteButton;
       String id;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            favouriteButton = itemView.findViewById(R.id.favouriteButton);
            groupNameField = itemView.findViewById(R.id.groupNameField);
        }

    }
    public void updateGroupsList(List<Group> groups){
        this.groupsList = groups;
        notifyDataSetChanged();
    }
    public List<Group> getGroupsList() {
        return groupsList;
    }
    public void updateUser(User user){
        this.user = user;
    }
}
