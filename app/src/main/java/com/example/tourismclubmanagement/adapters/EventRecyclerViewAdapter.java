package com.example.tourismclubmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourismclubmanagement.R;
import com.example.tourismclubmanagement.models.Event;
import com.example.tourismclubmanagement.models.EventInfo;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
        EventInfo eventInfo= eventsList.get(position).getEventInfo();
        holder.eventName.setText(eventInfo.getEventName());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String date = sdf.format(eventInfo.getDepartureTime());
        sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String time =sdf.format(eventInfo.getDepartureTime());
        holder.departureTime.setText(date +"\n" + time);
        holder.itemView.getLayoutParams().height = 300;
        holder.id = eventInfo.getId();
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
