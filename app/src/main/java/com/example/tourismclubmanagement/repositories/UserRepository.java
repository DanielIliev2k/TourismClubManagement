package com.example.tourismclubmanagement.repositories;

import androidx.annotation.NonNull;

import com.example.tourismclubmanagement.listeners.UserListener;
import com.example.tourismclubmanagement.models.User;
import com.example.tourismclubmanagement.models.UserGroup;
import com.example.tourismclubmanagement.models.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static final String USERS_NODE = "users";
    private final DatabaseReference usersRef;
    public UserRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://tourismclubmanagement-default-rtdb.europe-west1.firebasedatabase.app/");
        usersRef = database.getReference(USERS_NODE);
    }

    public void addUser(User user) {
        usersRef.child(user.getUserInfo().getId()).setValue(user);
    }

    public void addUserGroup(String userId, UserGroup userGroup){
        usersRef.child(userId).child("groups").child(userGroup.getGroupId()).setValue(userGroup);
    }
    public void deleteUserGroup(String userId, String groupId){
        usersRef.child(userId).child("groups").child(groupId).removeValue();
    }
    public void updateUser(UserInfo userInfo){
        userInfo.setFirstLogin(false);
        usersRef.child(userInfo.getId()).child("userInfo").setValue(userInfo);
    }
    public void getUser(String userId, final UserListener userListener){
        usersRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = new User();
                List<UserGroup> userGroups = new ArrayList<>();
                for (DataSnapshot groupSnapshot:snapshot.child("groups").getChildren()) {
                    UserGroup userGroup = groupSnapshot.getValue(UserGroup.class);
                    userGroups.add(userGroup);
                }
                UserInfo userInfo = snapshot.child("userInfo").getValue(UserInfo.class);
                user.setUserInfo(userInfo);
                user.setGroups(userGroups);
                userListener.onUserLoaded(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userListener.onFailure(error);
            }
        });
    }
    public void getUserOnce(String userId, final UserListener userListener){
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = new User();
                List<UserGroup> userGroups = new ArrayList<>();
                for (DataSnapshot groupSnapshot:snapshot.child("groups").getChildren()) {
                    UserGroup userGroup = groupSnapshot.getValue(UserGroup.class);
                    userGroups.add(userGroup);
                }
                UserInfo userInfo = snapshot.child("userInfo").getValue(UserInfo.class);
                user.setUserInfo(userInfo);
                user.setGroups(userGroups);
                userListener.onUserLoaded(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userListener.onFailure(error);
            }
        });
    }
    public void getAllUsers(final UserListener userListener){
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot userSnapshot:snapshot.getChildren()) {
                    User user = new User();
                    List<UserGroup> userGroups = new ArrayList<>();
                    for (DataSnapshot groupSnapshot:userSnapshot.child("groups").getChildren()) {
                        UserGroup userGroup = groupSnapshot.getValue(UserGroup.class);
                        userGroups.add(userGroup);
                    }
                    UserInfo userInfo = userSnapshot.child("userInfo").getValue(UserInfo.class);
                    user.setUserInfo(userInfo);
                    user.setGroups(userGroups);
                    users.add(user);
                }
                userListener.onAllUsersLoaded(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userListener.onFailure(error);
            }
        });
    }
    public void getAllUsersOnce(final UserListener userListener){
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot userSnapshot:snapshot.getChildren()) {
                    User user = new User();
                    List<UserGroup> userGroups = new ArrayList<>();
                    for (DataSnapshot groupSnapshot:userSnapshot.child("groups").getChildren()) {
                        UserGroup userGroup = groupSnapshot.getValue(UserGroup.class);
                        userGroups.add(userGroup);
                    }
                    UserInfo userInfo = userSnapshot.child("userInfo").getValue(UserInfo.class);
                    user.setUserInfo(userInfo);
                    user.setGroups(userGroups);
                    users.add(user);
                }
                userListener.onAllUsersLoaded(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userListener.onFailure(error);
            }
        });
    }
    public void changeUserGroupFavourite(String userId,String groupId,Boolean favouriteStatus){
        usersRef.child(userId).child("groups").child(groupId).child("favourite").setValue(favouriteStatus);
    }
    public void getUserIdByEmail(String email,final UserListener userListener){
        getAllUsersOnce(new UserListener() {
            @Override
            public void onAllUsersLoaded(List<User> users) {
                for (User user:users) {
                    if (user.getUserInfo().getEmail().equals(email)){
                        userListener.onUserIdLoadedByEmail(user.getUserInfo().getId());
                    }
                }
            }

            @Override
            public void onFailure(DatabaseError error) {
                userListener.onFailure(error);
            }
        });
    }
}
