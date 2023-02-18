package com.example.tourismclubmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourismclubmanagement.R;
import com.example.tourismclubmanagement.models.Event;

import java.util.ArrayList;
import java.util.List;

public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.ViewHolder> {

    private List<Event> eventsList;

    public CustomRecyclerViewAdapter(List<Event> eventsList) {
        this.eventsList = eventsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventsList.get(position);
        holder.eventName.setText(event.getEventName());
        holder.location.setText(event.getLocation());
        holder.duration.setText(event.getDuration());
        holder.notes.setText(event.getNotes());
        holder.departureTime.setText(event.getDepartureTime().toString());
        holder.equipment.setText(event.getEquipment());
        holder.itemView.getLayoutParams().height = 300;
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventName;
        TextView location;
        TextView duration;
        TextView notes;
        TextView departureTime;
        TextView equipment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            location = itemView.findViewById(R.id.location);
            duration = itemView.findViewById(R.id.duration);
            notes = itemView.findViewById(R.id.notes);
            departureTime = itemView.findViewById(R.id.departureTime);
            equipment = itemView.findViewById(R.id.equipment);
        }
    }
}
