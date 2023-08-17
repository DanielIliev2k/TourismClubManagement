package com.example.tourismclubmanagement.listeners;

import com.example.tourismclubmanagement.models.ChatMessage;
import com.example.tourismclubmanagement.models.EventParticipant;
import com.example.tourismclubmanagement.models.Group;
import com.example.tourismclubmanagement.models.UserInGroupInfo;
import com.google.firebase.database.DatabaseError;

import java.util.List;

public abstract class GroupListener {
    public void onGroupLoaded(Group group){

    }
    public void onGroupNameLoaded(String name){

    }
    public void onChatLoaded(List<ChatMessage> messages){

    }
    public void onUserInGroupLoaded(UserInGroupInfo userInGroup){

    }
    public void onEventParticipantLoaded(EventParticipant eventParticipant){

    }
    public void onAllEventParticipantsLoaded(List<EventParticipant> eventParticipants){

    }
    public void onGroupDeleted(String response){

    }
    public void onFailure(DatabaseError error){

    }
    public void onAllGroupNamesLoaded(List<String> groupNames){

    }
}
