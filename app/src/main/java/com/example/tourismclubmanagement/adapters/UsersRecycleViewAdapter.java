package com.example.tourismclubmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourismclubmanagement.R;
import com.example.tourismclubmanagement.models.User;

import java.util.List;

public class UsersRecycleViewAdapter extends RecyclerView.Adapter<UsersRecycleViewAdapter.ViewHolder>  {
    private List<User> usersList;
    private OnItemClickListener mListener;
    public interface OnItemClickListener {
        void onItemClick(String userId);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public UsersRecycleViewAdapter (List<User> usersList) {
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public UsersRecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list_item, parent, false);
        return new UsersRecycleViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersRecycleViewAdapter.ViewHolder holder, int position) {
        User user = usersList.get(position);
        holder.username.setText(user.getUsername());
        holder.id = user.getId();
        if (!user.getFirstLogin()){
            holder.name.setText(user.getName());
            holder.age.setText(user.getAge().toString());
            holder.hometown.setText(user.getHometown());
//        holder.eventApplications.setText(user.getEventApplications().toString());
//        holder.confirmedEvents.setText(user.getConfirmedEvents().toString());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the onItemClick method on the OnItemClickListener
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
        TextView username;
        TextView name;
        TextView age;
        TextView hometown;
        TextView eventApplications;
        TextView confirmedEvents;
        TextView isAdmin;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            name = itemView.findViewById(R.id.name);
            age = itemView.findViewById(R.id.age);
            hometown = itemView.findViewById(R.id.hometown);
            eventApplications = itemView.findViewById(R.id.eventApplications);
            confirmedEvents = itemView.findViewById(R.id.confirmedEvents);
            isAdmin = itemView.findViewById(R.id.isAdmin);
        }
    }
    public void updateUsersList(List<User> usersList){
        this.usersList = usersList;
        notifyDataSetChanged();
    }
}
