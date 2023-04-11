package com.example.tourismclubmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourismclubmanagement.R;
import com.example.tourismclubmanagement.models.Event;

import java.util.List;

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder> {

    private List<Event> eventsList;
    private OnItemClickListener mListener;
    public interface OnItemClickListener {
        void onItemClick(String eventId);

    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public EventRecyclerViewAdapter(List<Event> eventsList) {
        this.eventsList = eventsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_list_item, parent, false);
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
        holder.id = event.getId();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the onItemClick method on the OnItemClickListener
                if (mListener != null) {
                    mListener.onItemClick(holder.id);
                }
            }
        });
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
        String id;

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
    public void updateEventsList(List<Event> events){
        this.eventsList = events;
        notifyDataSetChanged();
}
    public List<Event> getEventsList() {
        return eventsList;
    }
}
