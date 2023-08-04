package com.example.tourismclubmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourismclubmanagement.R;
import com.example.tourismclubmanagement.models.EventParticipant;
import com.example.tourismclubmanagement.models.Status;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class ParticipantRecyclerViewAdapter extends RecyclerView.Adapter<ParticipantRecyclerViewAdapter.ViewHolder>  {
    private List<EventParticipant> participants;
    private String eventId;
    private DatabaseReference eventsDatasource;

    public ParticipantRecyclerViewAdapter(List<EventParticipant> participants,String eventId,DatabaseReference eventsDatasource) {
        this.participants = participants;
        this.eventId = eventId;
        this.eventsDatasource = eventsDatasource;
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
        holder.participantName.setText(participant.getUserName());
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
                eventsDatasource.child(eventId).child("participants").child(participant.getUserId()).setValue(participant);
                holder.acceptButton.setVisibility(View.INVISIBLE);
                holder.denyButton.setVisibility(View.VISIBLE);
                holder.participationStatus.setText(participant.getStatus().toString());
            }
        });
        holder.denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                participant.setStatus(Status.DENIED);
                eventsDatasource.child(eventId).child("participants").child(participant.getUserId()).setValue(participant);
                holder.denyButton.setVisibility(View.INVISIBLE);
                holder.acceptButton.setVisibility(View.VISIBLE);
                holder.participationStatus.setText(participant.getStatus().toString());
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
