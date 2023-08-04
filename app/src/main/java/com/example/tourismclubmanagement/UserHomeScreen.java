package com.example.tourismclubmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tourismclubmanagement.adapters.UserGroupRecyclerViewAdapter;
import com.example.tourismclubmanagement.comparators.GroupComparator;
import com.example.tourismclubmanagement.models.ChatMessage;
import com.example.tourismclubmanagement.models.Event;
import com.example.tourismclubmanagement.models.Group;
import com.example.tourismclubmanagement.models.GroupInfo;
import com.example.tourismclubmanagement.models.Role;
import com.example.tourismclubmanagement.models.User;
import com.example.tourismclubmanagement.models.UserGroup;
import com.example.tourismclubmanagement.models.UserInGroupInfo;
import com.example.tourismclubmanagement.models.UserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class UserHomeScreen extends AppCompatActivity {
    private FirebaseUser userAuth;
    private  FirebaseAuth mAuth;
    private User user;
    private DatabaseReference usersDatasource;
    private DatabaseReference groupsDatasource;
    private List<Group> currentUserGroups;
    private Dialog newGroupNamePopup;
    private UserGroupRecyclerViewAdapter userGroupRecycleViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_screen);
        instantiate();
        getLoggedInUserInfo(userAuth.getUid());
        displayUserGroups();
    }

    public void displayUserGroups() {
        RecyclerView userGroupsContainer;
        userGroupsContainer = findViewById(R.id.userGroupsContainer);
        userGroupsContainer.setLayoutManager(new LinearLayoutManager(this));
        userGroupRecycleViewAdapter = new UserGroupRecyclerViewAdapter(currentUserGroups, user);
        userGroupsContainer.setAdapter(userGroupRecycleViewAdapter);
    }
    public void showCreateGroupPopup() {
        newGroupNamePopup = new Dialog(UserHomeScreen.this);
        newGroupNamePopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        newGroupNamePopup.setCancelable(true);
        newGroupNamePopup.setContentView(R.layout.create_new_group_popup);
        int width = (int) (getResources().getDisplayMetrics().widthPixels);
        int height = newGroupNamePopup.getWindow().getAttributes().height;
        newGroupNamePopup.getWindow().setLayout(width,height);
        newGroupNamePopup.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                newGroupNamePopup = new Dialog(UserHomeScreen.this);
            }
        });
        AppCompatButton cancelPopupButton = newGroupNamePopup.findViewById(R.id.cancelPopupButton);
        cancelPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGroupNamePopup.cancel();
            }
        });
        AppCompatButton submitGroupButton = newGroupNamePopup.findViewById(R.id.submitGroupButton);
        submitGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               createNewGroup();
            }
        });
        newGroupNamePopup.show();
    }
    public void generateButtons(){

        AppCompatButton createNewGroupButton = findViewById(R.id.createNewGroupButton);
        AppCompatButton settingsButton = findViewById(R.id.settingsButton);
        AppCompatButton logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent loginIntent = new Intent(UserHomeScreen.this,LoginScreen.class);
                startActivity(loginIntent);
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userEditInfoIntent = new Intent(UserHomeScreen.this,UserEditInfoScreen.class);
                startActivity(userEditInfoIntent);
            }
        });
        createNewGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateGroupPopup();
            }
        });

    }

    public void createNewGroup(){
        AppCompatEditText newGroupNameField = newGroupNamePopup.findViewById(R.id.newGroupNameField);
        TextView groupNameError = newGroupNamePopup.findViewById(R.id.groupNameError);
        groupNameError.setVisibility(View.INVISIBLE);
        if (!(Objects.requireNonNull(newGroupNameField.getText()).toString().equals("")) ){
            Group group = new Group();
            String groupId = groupsDatasource.push().getKey();
            group.setGroupInfo(new GroupInfo(groupId,newGroupNameField.getText().toString(),new Date()));
            assert groupId != null;
            groupsDatasource.child(groupId).setValue(group);
            groupsDatasource.child(groupId).child("usersInGroup").child(user.getUserInfo().getId()).setValue(new UserInGroupInfo(user.getUserInfo().getId(), Role.OWNER));
            usersDatasource.child(user.getUserInfo().getId()).child("groups").child(groupId).setValue(new UserGroup(group.getGroupInfo().getId(),false));
            newGroupNamePopup.cancel();
        }
        if ((Objects.requireNonNull(newGroupNameField.getText()).toString().equals(""))) {
            groupNameError.setText("Please type a name for the group");
            groupNameError.setVisibility(View.VISIBLE);
        }
    }
    public void getUserGroups() {
                groupsDatasource.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (user.getGroups()!=null){
                            currentUserGroups.clear();
                            for (UserGroup userGroup: user.getGroups()) {
                                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                    GroupInfo groupInfo = dataSnapshot.child("groupInfo").getValue(GroupInfo.class);
                                    if (groupInfo.getId().equals(userGroup.getGroupId())){
                                        Group group = new Group();
                                        group.setGroupInfo(groupInfo);
                                        List<Event> events = new ArrayList<>();
                                        List<ChatMessage> chatMessages = new ArrayList<>();
                                        group.setEvents(events);
                                        List<UserInGroupInfo> usersInGroup = new ArrayList<>();
                                        for (DataSnapshot userInGroupData:snapshot.child("usersInGroup").getChildren()) {
                                            usersInGroup.add(userInGroupData.getValue(UserInGroupInfo.class));
                                        }
                                        for (DataSnapshot chatMessage:snapshot.child("chat").getChildren()){
                                            chatMessages.add(chatMessage.getValue(ChatMessage.class));
                                        }
                                        group.setUsersInGroup(usersInGroup);
                                        group.setChat(chatMessages);
                                        currentUserGroups.add(group);
                                        currentUserGroups.sort(new GroupComparator(user));
                                        userGroupRecycleViewAdapter.updateGroupsList(currentUserGroups);
                                        groupsSetOnClickListener();
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public void groupsSetOnClickListener(){
        userGroupRecycleViewAdapter.setOnItemClickListener(new UserGroupRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String groupId) {
                Intent inGroupScreenIntent = new Intent(UserHomeScreen.this,InGroupScreen.class);
                inGroupScreenIntent.putExtra("groupId",groupId);
                startActivity(inGroupScreenIntent);
            }
        });
    }

    public void instantiate(){
        mAuth = FirebaseAuth.getInstance();
        userAuth = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://tourismclubmanagement-default-rtdb.europe-west1.firebasedatabase.app/");
        usersDatasource = database.getReference("users");
        groupsDatasource = database.getReference("groups");
        currentUserGroups = new ArrayList<>();
        generateButtons();
    }
    public void getLoggedInUserInfo(String userId){
        usersDatasource.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = new User();
                UserInfo userInfo = dataSnapshot.child("userInfo").getValue(UserInfo.class);
                List<UserGroup> groups = new ArrayList<>();
                for (DataSnapshot snapshot:dataSnapshot.child("groups").getChildren()) {
                    groups.add(snapshot.getValue(UserGroup.class));
                }
                user.setUserInfo(userInfo);
                user.setGroups(groups);
                TextView userEmailField = findViewById(R.id.userEmailField);
                userEmailField.setText(user.getUserInfo().getEmail());
                getUserGroups();
                userGroupRecycleViewAdapter.updateUser(user);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserHomeScreen.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}