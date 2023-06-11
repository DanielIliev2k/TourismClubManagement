package com.example.tourismclubmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourismclubmanagement.R;
import com.example.tourismclubmanagement.models.Event;

import java.text.SimpleDateFormat;
import java.util.Date;
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = sdf.format(event.getDepartureTime());
        holder.departureTime.setText(date);
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
        TextView departureTime;
        String id;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            departureTime = itemView.findViewById(R.id.departureTime);
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
