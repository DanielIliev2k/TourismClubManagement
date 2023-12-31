package com.example.tourismclubmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourismclubmanagement.R;
import com.example.tourismclubmanagement.models.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder> {

    private List<ChatMessage> messages;

    public ChatRecyclerViewAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StringBuilder messageHeader = new StringBuilder();
        ChatMessage message = messages.get(position);
        String sender = "";
        if (message.getSender().length()>=21){
            sender = message.getSender().substring(0,20) + "...";
        }
        else {
            sender = message.getSender();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm", Locale.ENGLISH);
        messageHeader.append(dateFormat.format(message.getDate()) + " " + sender + " : ");
        holder.chatMessageHeader.setText(messageHeader);
        holder.chatMessage.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
    TextView chatMessageHeader;
    TextView chatMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chatMessage = itemView.findViewById(R.id.chatMessage);
            chatMessageHeader = itemView.findViewById(R.id.chatMessageHeader);
        }

    }
    public void updateMessages(List<ChatMessage> messages){
        this.messages = messages;
        notifyDataSetChanged();
    }
}