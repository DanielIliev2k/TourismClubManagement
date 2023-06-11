package com.example.tourismclubmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourismclubmanagement.R;
import com.example.tourismclubmanagement.models.Group;
import com.example.tourismclubmanagement.models.GroupInfo;

import java.util.List;

public class UserGroupRecyclerViewAdapter extends RecyclerView.Adapter<UserGroupRecyclerViewAdapter.ViewHolder> {

    private List<Group> groupsList;
    private OnItemClickListener mListener;
    public interface OnItemClickListener {
        void onItemClick(String groupId);

    }
    public void setOnItemClickListener(UserGroupRecyclerViewAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public UserGroupRecyclerViewAdapter(List<Group> groupsList) {
        this.groupsList = groupsList;
    }

    @NonNull
    @Override
    public UserGroupRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_groups_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserGroupRecyclerViewAdapter.ViewHolder holder, int position) {
        Group group = groupsList.get(position);
        GroupInfo groupInfo = group.getGroupInfo();
        holder.groupNameField.setText( groupInfo.getGroupName());
        holder.id = groupInfo.getId();
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
        return groupsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       TextView groupNameField;
       String id;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
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
}
