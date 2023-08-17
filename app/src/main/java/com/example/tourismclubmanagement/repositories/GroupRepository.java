package com.example.tourismclubmanagement.repositories;

import androidx.annotation.NonNull;

import com.example.tourismclubmanagement.listeners.GroupListener;
import com.example.tourismclubmanagement.listeners.UserListener;
import com.example.tourismclubmanagement.models.ChatMessage;
import com.example.tourismclubmanagement.models.Event;
import com.example.tourismclubmanagement.models.EventInfo;
import com.example.tourismclubmanagement.models.EventParticipant;
import com.example.tourismclubmanagement.models.Group;
import com.example.tourismclubmanagement.models.GroupInfo;
import com.example.tourismclubmanagement.models.Role;
import com.example.tourismclubmanagement.models.Status;
import com.example.tourismclubmanagement.models.User;
import com.example.tourismclubmanagement.models.UserGroup;
import com.example.tourismclubmanagement.models.UserInGroupInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupRepository {
    private static final String GROUPS_NODE = "groups";
    private final DatabaseReference groupsRef;
    private final UserRepository userRepository;

    public GroupRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://tourismclubmanagement-default-rtdb.europe-west1.firebasedatabase.app/");
        groupsRef = database.getReference(GROUPS_NODE);
        userRepository = new UserRepository();
    }
    public void getChat(String groupId,final GroupListener groupListener){
        List<ChatMessage> messages = new ArrayList<>();
        groupsRef.child(groupId).child("chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot messageSnapshot:snapshot.getChildren()) {
                    messages.add(messageSnapshot.getValue(ChatMessage.class));
                }
                groupListener.onChatLoaded(messages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                groupListener.onFailure(error);
            }
        });
    }
    public void addGroup(String userId, String groupName){
        Group group = new Group();
        String groupId = groupsRef.push().getKey();
        assert groupId != null;
        GroupInfo groupInfo = new GroupInfo(groupId,groupName,new Date());
        group.setGroupInfo(groupInfo);
        groupsRef.child(groupId).setValue(group);
        addUserToGroup(groupId,userId,Role.OWNER);
    }
    public void getGroup(String groupId, final GroupListener groupListener){
        groupsRef.child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = new Group();
                List<Event> events = new ArrayList<>();
                for (DataSnapshot eventSnapshot:snapshot.child("events").getChildren()) {
                    Event event = new Event();
                    List<EventParticipant> participants = new ArrayList<>();
                    for (DataSnapshot participantSnapshot:eventSnapshot.child("participants").getChildren()) {
                        EventParticipant participant = participantSnapshot.getValue(EventParticipant.class);
                        participants.add(participant);
                    }
                    EventInfo eventInfo = eventSnapshot.child("eventInfo").getValue(EventInfo.class);
                    event.setEventInfo(eventInfo);
                    event.setParticipants(participants);
                    events.add(event);
                }
                GroupInfo groupInfo = snapshot.child("groupInfo").getValue(GroupInfo.class);
                List<UserInGroupInfo> usersInGroup = new ArrayList<>();
                for (DataSnapshot userInGroupSnapshot:snapshot.child("usersInGroup").getChildren()) {
                    UserInGroupInfo userInGroup = userInGroupSnapshot.getValue(UserInGroupInfo.class);
                    usersInGroup.add(userInGroup);
                }
                List<ChatMessage> chat = new ArrayList<>();
                for (DataSnapshot chatSnapshot:snapshot.child("chat").getChildren()) {
                    ChatMessage chatMessage = chatSnapshot.getValue(ChatMessage.class);
                    chat.add(chatMessage);
                }
                group.setGroupInfo(groupInfo);
                group.setUsersInGroup(usersInGroup);
                group.setChat(chat);
                group.setEvents(events);
                groupListener.onGroupLoaded(group);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                groupListener.onFailure(error);
            }
        });
    }
    public void getGroupOnce(String groupId, final GroupListener groupListener){
        groupsRef.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = new Group();
                List<Event> events = new ArrayList<>();
                for (DataSnapshot eventSnapshot:snapshot.child("events").getChildren()) {
                    Event event = new Event();
                    List<EventParticipant> participants = new ArrayList<>();
                    for (DataSnapshot participantSnapshot:eventSnapshot.child("participants").getChildren()) {
                        EventParticipant participant = participantSnapshot.getValue(EventParticipant.class);
                        participants.add(participant);
                    }
                    EventInfo eventInfo = eventSnapshot.child("eventInfo").getValue(EventInfo.class);
                    event.setEventInfo(eventInfo);
                    event.setParticipants(participants);
                    events.add(event);
                }
                GroupInfo groupInfo = snapshot.child("groupInfo").getValue(GroupInfo.class);
                List<UserInGroupInfo> usersInGroup = new ArrayList<>();
                for (DataSnapshot userInGroupSnapshot:snapshot.child("usersInGroup").getChildren()) {
                    UserInGroupInfo userInGroup = userInGroupSnapshot.getValue(UserInGroupInfo.class);
                    usersInGroup.add(userInGroup);
                }
                List<ChatMessage> chat = new ArrayList<>();
                for (DataSnapshot chatSnapshot:snapshot.child("chat").getChildren()) {
                    ChatMessage chatMessage = chatSnapshot.getValue(ChatMessage.class);
                    chat.add(chatMessage);
                }
                group.setGroupInfo(groupInfo);
                group.setUsersInGroup(usersInGroup);
                group.setChat(chat);
                group.setEvents(events);
                groupListener.onGroupLoaded(group);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                groupListener.onFailure(error);
            }
        });
    }
    public void deleteGroup(String groupId,final GroupListener groupListener){
        getGroupOnce(groupId, new GroupListener() {
            @Override
            public void onGroupLoaded(Group group) {
                for (UserInGroupInfo userInGroup:group.getUsersInGroup()) {
                    userRepository.deleteUserGroup(userInGroup.getId(),groupId);
                }
                groupsRef.child(groupId).removeValue();
                groupListener.onGroupDeleted("Deleted");
            }

            @Override
            public void onFailure(DatabaseError error) {
                groupListener.onFailure(error);
            }
        });

    }
    public void addUserToGroup(String groupId, String userId, Role userRole){
        userRepository.getUserOnce(userId, new UserListener() {
            @Override
            public void onUserLoaded(User user) {
                UserInGroupInfo userInGroupInfo = new UserInGroupInfo(userId,userRole);
                groupsRef.child(groupId).child("usersInGroup").child(userId).setValue(userInGroupInfo);
                userRepository.addUserGroup(userId,new UserGroup(groupId,false));
            }

            @Override
            public void onFailure(DatabaseError error) {

            }
        });

    }

    public void updateUserLastLogin(String groupId, String userId){
        groupsRef.child(groupId).child("usersInGroup").child(userId).child("lastLogin").setValue(new Date());
    }
    public void updateGroupName(String groupId,String newName){
        groupsRef.child(groupId).child("groupInfo").child("groupName").setValue(newName);
    }
    public void addEvent(String groupId, EventInfo eventInfo){
        String eventId = groupsRef.child(groupId).child("events").push().getKey();
        eventInfo.setId(eventId);
        groupsRef.child(groupId).child("events").child(eventInfo.getId()).child("eventInfo").setValue(eventInfo);
    }
    public void updateEvent(String groupId, EventInfo eventInfo){
        groupsRef.child(groupId).child("events").child(eventInfo.getId()).child("eventInfo").setValue(eventInfo);
    }
    public void deleteEvent(String groupId,String eventId){
        groupsRef.child(groupId).child("events").child(eventId).removeValue();
    }
    public void addEventParticipant(String groupId, String eventId, EventParticipant eventParticipant){
        groupsRef.child(groupId).child("events").child(eventId).child("participants").child(eventParticipant.getUserId()).setValue(eventParticipant);
    }
    public void updateEventParticipantStatus(String groupId, String eventId, String userId, Status newStatus){
        groupsRef.child(groupId).child("events").child(eventId).child("participants").child(userId).child("status").setValue(newStatus);
    }
    public void deleteParticipant(String groupId, String eventId, String userId){
        groupsRef.child(groupId).child("events").child(eventId).child("participants").child(userId).removeValue();
    }
    public void addChatMessage(String groupId,String userName,String message){
        Date date = new Date();
        groupsRef.child(groupId).child("chat").child(date.toString()).setValue(new ChatMessage(userName,date,message));
    }
    public void removeUserFromGroup(String groupId,String userId){
        getUserInGroupOnce(groupId, userId, new GroupListener() {
                    @Override
                    public void onUserInGroupLoaded(UserInGroupInfo userInGroup) {
                        getGroup(groupId, new GroupListener() {
                            @Override
                            public void onGroupLoaded(Group group) {
                                for (Event event:group.getEvents()) {
                                    deleteParticipant(groupId,event.getEventInfo().getId(),userId);
                                }
                                groupsRef.child(groupId).child("usersInGroup").child(userId).removeValue();
                                userRepository.deleteUserGroup(userId,groupId);
                            }
                        });

                    }

            @Override
            public void onFailure(DatabaseError error) {

            }
        });

    }
    public void getGroupName(String groupId,GroupListener groupListener){
        groupsRef.child(groupId).child("groupInfo").child("groupName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupListener.onGroupNameLoaded(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                groupListener.onFailure(error);
            }
        });
    }
    public void getUserInGroupOnce(String groupId,String userId,final GroupListener groupListener){
        groupsRef.child(groupId).child("usersInGroup").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserInGroupInfo userInGroup = snapshot.getValue(UserInGroupInfo.class);
                groupListener.onUserInGroupLoaded(userInGroup);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                groupListener.onFailure(error);
            }
        });
    }
    public void changeUserRole(String groupId,String userId){
        getUserInGroupOnce(groupId, userId, new GroupListener() {
            @Override
            public void onUserInGroupLoaded(UserInGroupInfo userInGroup) {
                if (userInGroup.getRole().equals(Role.USER)){
                    groupsRef.child(groupId).child("usersInGroup").child(userId).child("role").setValue(Role.ADMIN);
                } else if (userInGroup.getRole().equals(Role.ADMIN)) {
                    groupsRef.child(groupId).child("usersInGroup").child(userId).child("role").setValue(Role.USER);
                }
            }

            @Override
            public void onFailure(DatabaseError error) {

            }
        });
    }
    public void getEventParticipant(String groupId,String eventId,String userId,final GroupListener groupListener){
        groupsRef.child(groupId).child("events").child(eventId).child("participants").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                EventParticipant eventParticipant = snapshot.getValue(EventParticipant.class);
                groupListener.onEventParticipantLoaded(eventParticipant);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                groupListener.onFailure(error);
            }
        });
    }
    public void getAllEventParticipants(String groupId,String eventId,final GroupListener groupListener){
        groupsRef.child(groupId).child("events").child(eventId).child("participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<EventParticipant> eventParticipants = new ArrayList<>();
                for (DataSnapshot participantSnapshot:snapshot.getChildren()) {
                    eventParticipants.add(participantSnapshot.getValue(EventParticipant.class));
                }
                groupListener.onAllEventParticipantsLoaded(eventParticipants);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                groupListener.onFailure(error);
            }
        });
    }
    public void getAllGroupNamesOnce(final GroupListener groupListener){
        groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> groupNames = new ArrayList<>();
                for (DataSnapshot groupSnapshot:snapshot.getChildren()) {
                    groupNames.add(groupSnapshot.child("groupInfo").child("groupName").getValue(String.class));
                }
                groupListener.onAllGroupNamesLoaded(groupNames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                groupListener.onFailure(error);
            }
        });
    }
}
