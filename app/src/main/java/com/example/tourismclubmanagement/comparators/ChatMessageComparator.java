package com.example.tourismclubmanagement.comparators;

import com.example.tourismclubmanagement.models.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Locale;

public class ChatMessageComparator implements Comparator<ChatMessage> {
        public ChatMessageComparator() {
        }

        @Override
        public int compare(ChatMessage message1, ChatMessage message2) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyyHH:mm:ss", Locale.ENGLISH);

            String date1 = sdf.format(message1.getDate());
            String date2 = sdf.format(message2.getDate());

            return date1.compareToIgnoreCase(date2);
        }

}
