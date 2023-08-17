package com.example.tourismclubmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourismclubmanagement.R;
import com.example.tourismclubmanagement.listeners.GroupListener;
import com.example.tourismclubmanagement.models.User;
import com.example.tourismclubmanagement.models.UserGroup;
import com.example.tourismclubmanagement.repositories.GroupRepository;
import com.example.tourismclubmanagement.repositories.UserRepository;
import com.google.firebase.database.DatabaseError;

public class UserGroupRecyclerViewAdapter extends RecyclerView.Adapter<UserGroupRecyclerViewAdapter.ViewHolder> {
    private User user;
    private OnItemClickListener mListener;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    public interface OnItemClickListener {
        void onItemClick(String groupId);

    }
    public void setOnItemClickListener(UserGroupRecyclerViewAdapter.OnItemClickListener listener) {
        mListener = listener;
    }
    public UserGroupRecyclerViewAdapter(User user,UserRepository userRepository,GroupRepository groupRepository) {
        this.user = user;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @NonNull
    @Override
    public UserGroupRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_groups_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserGroupRecyclerViewAdapter.ViewHolder holder, int position) {
        UserGroup userGroup = user.getGroups().get(position);
        String userId = user.getUserInfo().getId();
        groupRepository.getGroupName(userGroup.getGroupId(), new GroupListener() {
            @Override
            public void onGroupNameLoaded(String name) {
                holder.groupNameField.setText(name);
            }
            @Override
            public void onFailure(DatabaseError error) {
                Toast.makeText(holder.itemView.getContext(),"Database Error!",Toast.LENGTH_LONG).show();
            }
        });
        if (userGroup.getFavourite()){
            holder.favouriteButton.setBackgroundResource(R.drawable.favourite_star_active);
        }
        else {
            holder.favouriteButton.setBackgroundResource(R.drawable.favourite_star_inactive);
        }
        holder.favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userGroup.getFavourite()){
                    userRepository.changeUserGroupFavourite(userId,userGroup.getGroupId(),false);
                }
                else {
                    userRepository.changeUserGroupFavourite(userId,userGroup.getGroupId(),true);
                }

            }
        });
        holder.id = userGroup.getGroupId();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(holder.id);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (user!=null){
            return user.getGroups().size();
        }
        return 0;
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
    public void updateUser(User user){
        this.user = user;
        notifyDataSetChanged();
    }
}
