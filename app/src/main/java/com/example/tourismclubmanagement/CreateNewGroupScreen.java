package com.example.tourismclubmanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.tourismclubmanagement.models.Group;
import com.example.tourismclubmanagement.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CreateNewGroupScreen extends AppCompatActivity {
    private TextInputEditText usernameField;
    private TextInputEditText passwordField;
    private TextInputEditText groupNameField;
    private AppCompatButton createGroupButton;
    private FirebaseDatabase database;
    private DatabaseReference groupsDatasource;
    private DatabaseReference usersDatasource;
    private ArrayList<String> groupUserIds;
    private Intent loginIntent;

    private User user;
    private Group group;
    private Intent firstLoginEditUserIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group_screen);
        database = FirebaseDatabase.getInstance("https://tourismclubmanagement-default-rtdb.europe-west1.firebasedatabase.app/");
        groupsDatasource = database.getReference("groups");
        usersDatasource = database.getReference("users");
        groupUserIds = new ArrayList<>();
        instantiate();
        setButtonOnClickListeners();
    }
    public void setButtonOnClickListeners(){
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup();
                addUserToDb();
                group.setUserIds(groupUserIds);
                groupsDatasource.child(group.getId()).setValue(group);
                if (user.getFirstLogin())
                {
                    startFirstLoginEditUser();
                }
                else {
                    logNewUserIn();
                }
            }
        });

    }
    public void startFirstLoginEditUser(){
        firstLoginEditUserIntent = new Intent(this,FirstLoginUserEditInfoScreen.class);
        firstLoginEditUserIntent.putExtra("user",user);
        this.startActivity(firstLoginEditUserIntent);
    }
    public void instantiate(){
        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        createGroupButton = findViewById(R.id.createGroupButton);
        groupNameField = findViewById(R.id.groupNameField);
        loginIntent = new Intent(this,MainScreen.class);
    }
    public void addUserToDb(){
        String userId = usersDatasource.push().getKey();
        user = new User(group.getId(),userId);
        user.setUsername(usernameField.getText().toString());
        user.setPassword(passwordField.getText().toString());
        user.setAdmin(true);
        groupUserIds.add(userId);
        usersDatasource.child(userId).setValue(user);
    }
    public void createGroup(){
        String groupId = groupsDatasource.push().getKey();
        group = new Group(groupId,new Date());
        group.setGroupName(groupNameField.getText().toString());
        group.setUserIds(new ArrayList<>());
        group.setEvents(new HashMap<>());
    }
    public void logNewUserIn(){
        loginIntent.putExtra("user",user);
        startActivity(loginIntent);
    }
}