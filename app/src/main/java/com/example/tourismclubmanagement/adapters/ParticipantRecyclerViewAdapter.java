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
import com.example.tourismclubmanagement.listeners.UserListener;
import com.example.tourismclubmanagement.models.EventParticipant;
import com.example.tourismclubmanagement.models.Status;
import com.example.tourismclubmanagement.models.User;
import com.example.tourismclubmanagement.models.UserInfo;
import com.example.tourismclubmanagement.repositories.GroupRepository;
import com.example.tourismclubmanagement.repositories.UserRepository;
import com.google.firebase.database.DatabaseError;

import java.util.List;

public class ParticipantRecyclerViewAdapter extends RecyclerView.Adapter<ParticipantRecyclerViewAdapter.ViewHolder>  {
    private List<EventParticipant> participants;
    private String eventId;
    private String groupId;
    private GroupRepository groupRepository;
    private UserRepository userRepository;

    public ParticipantRecyclerViewAdapter(List<EventParticipant> participants,String eventId,String groupId,GroupRepository groupRepository,UserRepository userRepository) {
        this.participants = participants;
        this.eventId = eventId;
        this.groupId = groupId;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    @NonNull
    @Override
    public ParticipantRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.participants_list_item, parent, false);
        return new ParticipantRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantRecyclerViewAdapter.ViewHolder holder, int position) {
        EventParticipant participant = participants.get(position);
        userRepository.getUser(participant.getUserId(), new UserListener() {
            @Override
            public void onUserLoaded(User user) {
                UserInfo userInfo = user.getUserInfo();
                String[] userName = userInfo.getName().split(" ",2);
                if (userName.length>1){
                    holder.participantName.setText(userName[0] + "\n" + userName[1]);
                }
                else {
                    holder.participantName.setText(userInfo.getName());
                }
                switch (participant.getStatus()){
                    case ACCEPTED:
                        holder.acceptButton.setVisibility(View.INVISIBLE);
                        break;
                    case DENIED:
                        holder.denyButton.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        holder.acceptButton.setVisibility(View.VISIBLE);
                        holder.denyButton.setVisibility(View.VISIBLE);
                        break;
                }
                holder.participationStatus.setText(participant.getStatus().toString());
                holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        participant.setStatus(Status.ACCEPTED);
                        groupRepository.updateEventParticipantStatus(groupId,eventId, participant.getUserId(), Status.ACCEPTED);
                        holder.acceptButton.setVisibility(View.INVISIBLE);
                        holder.denyButton.setVisibility(View.VISIBLE);
                        holder.participationStatus.setText(participant.getStatus().toString());
                    }
                });
                holder.denyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        participant.setStatus(Status.DENIED);
                        groupRepository.updateEventParticipantStatus(groupId,eventId, participant.getUserId(), Status.DENIED);
                        holder.denyButton.setVisibility(View.INVISIBLE);
                        holder.acceptButton.setVisibility(View.VISIBLE);
                        holder.participationStatus.setText(participant.getStatus().toString());
                    }
                });

            }
            @Override
            public void onFailure(DatabaseError error) {
                Toast.makeText(holder.itemView.getContext(),"Database Error!",Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView participantName;
        TextView participationStatus;
        AppCompatButton acceptButton;
        AppCompatButton denyButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            participantName = itemView.findViewById(R.id.participantName);
            participationStatus = itemView.findViewById(R.id.participationStatusField);
            acceptButton = itemView.findViewById(R.id.acceptParticipationButton);
            denyButton = itemView.findViewById(R.id.denyParticipationButton);
        }

    }
    public void updateParticipants(List<EventParticipant> participants){
        this.participants = participants;
        notifyDataSetChanged();
    }
}
