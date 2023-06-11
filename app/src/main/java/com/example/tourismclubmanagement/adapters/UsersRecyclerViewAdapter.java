package com.example.tourismclubmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourismclubmanagement.R;
import com.example.tourismclubmanagement.models.Role;
import com.example.tourismclubmanagement.models.User;
import com.example.tourismclubmanagement.models.UserInGroupInfo;

import java.util.List;

public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<UsersRecyclerViewAdapter.ViewHolder>  {
    private List<User> usersList;
    private  List<UserInGroupInfo> usersInGroupList;
    private OnItemClickListener mListener;
    public interface OnItemClickListener {
        void onItemClick(String userId);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public UsersRecyclerViewAdapter(List<User> usersList, List<UserInGroupInfo> usersInGroupList) {
        this.usersList = usersList;
        this.usersInGroupList = usersInGroupList;
    }

    @NonNull
    @Override
    public UsersRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list_item, parent, false);
        return new UsersRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersRecyclerViewAdapter.ViewHolder holder, int position) {
        User user = usersList.get(position);
        holder.id = user.getId();
        if (!user.getFirstLogin()){
            holder.name.setText(user.getName());
            holder.age.setText(user.getAge().toString());
            holder.hometown.setText(user.getHometown());
            for (UserInGroupInfo userInGroup:usersInGroupList) {
                if (userInGroup.getId().equals(user.getId())){
                    if (userInGroup.getRole().equals(Role.OWNER)){
                        holder.itemView.setBackgroundResource(R.drawable.owner_item_background);
                    } else if (userInGroup.getRole().equals(Role.ADMIN)){
                        holder.itemView.setBackgroundResource(R.drawable.admin_item_background);
                    }
                    else {
                        holder.itemView.setBackgroundResource(R.drawable.user_item_background);
                    }
                    break;
                }
            }

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(holder.id);
                }
            }
        });

        holder.itemView.getLayoutParams().height = 300;
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        String id;
        TextView name;
        TextView age;
        TextView hometown;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            age = itemView.findViewById(R.id.age);
            hometown = itemView.findViewById(R.id.hometown);
        }
    }
    public void updateUsersList(List<User> usersList){
        this.usersList = usersList;
        notifyDataSetChanged();
    }
    public void updateUsersInGroupList(List<UserInGroupInfo> usersInGroupList){
        this.usersInGroupList = usersInGroupList;
    }
}
