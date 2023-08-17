package com.example.tourismclubmanagement.listeners;

import com.example.tourismclubmanagement.models.User;
import com.google.firebase.database.DatabaseError;

import java.util.List;

public abstract class UserListener {
    public void onUserLoaded(User user){

    };
    public void onAllUsersLoaded(List<User> users){

    };
    public void onUserIdLoadedByEmail(String  userId){

    }
    public void onUserAccountDeleted(String response){

    }
    public void onAllUserGroupsDeleted(String response){

    }
    public void onFailure(DatabaseError error){

    }
}
