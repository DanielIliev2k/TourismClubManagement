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
import com.example.tourismclubmanagement.models.Event;
import com.example.tourismclubmanagement.models.Group;
import com.example.tourismclubmanagement.models.GroupInfo;
import com.example.tourismclubmanagement.models.Role;
import com.example.tourismclubmanagement.models.User;
import com.example.tourismclubmanagement.models.UserInGroupInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class UserHomeScreen extends AppCompatActivity {
    private FirebaseUser userAuth;
    private  FirebaseAuth mAuth;
    private User userInfo;
    private StorageReference storageReference;
    private DatabaseReference usersDatasource;
    private DatabaseReference groupsDatasource;
    private List<Group> currentUserGroups;
    private Dialog newGroupNamePopup;
    private Dialog deleteGroupPopup;
    private Boolean deleteGroupMode;
    private UserGroupRecyclerViewAdapter userGroupRecycleViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_screen);
        instantiate();
        getLoggedInUserInfo(userAuth.getUid());
        getUserGroups();
        displayUserGroups();
    }

    public void displayUserGroups() {
        RecyclerView userGroupsContainer;
        userGroupsContainer = findViewById(R.id.userGroupsContainer);
        userGroupsContainer.setLayoutManager(new LinearLayoutManager(this));
        userGroupRecycleViewAdapter = new UserGroupRecyclerViewAdapter(currentUserGroups);
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
        AppCompatButton deleteGroupButton = findViewById(R.id.deleteGroupButton);
        AppCompatButton logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent loginIntent = new Intent(UserHomeScreen.this,LoginScreen.class);
                startActivity(loginIntent);
            }
        });
        deleteGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteGroupMode){
                    deleteGroupButton.setBackgroundResource(R.drawable.button_background);
                }
                else {
                    deleteGroupButton.setBackgroundResource(R.drawable.delete_button_background);
                }
                deleteGroupMode=!deleteGroupMode;
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
                deleteGroupButton.setBackgroundResource(R.drawable.button_background);
                showCreateGroupPopup();
            }
        });

    }

    private void showDeleteGroupPopup(String groupId) {
        deleteGroupPopup = new Dialog(UserHomeScreen.this);
        deleteGroupPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deleteGroupPopup.setCancelable(true);
        deleteGroupPopup.setContentView(R.layout.delete_group_popup);
        int width = (int) (getResources().getDisplayMetrics().widthPixels);
        int height = deleteGroupPopup.getWindow().getAttributes().height;
        deleteGroupPopup.getWindow().setLayout(width,height);
        deleteGroupPopup.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                deleteGroupPopup = new Dialog(UserHomeScreen.this);
            }
        });
        AppCompatButton cancelPopupButton = deleteGroupPopup.findViewById(R.id.cancelPopupButton);
        cancelPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteGroupPopup.cancel();
            }
        });
        Group group = new Group();
        for (Group tempGroup:currentUserGroups) {
            if (tempGroup.getGroupInfo().getId().equals(groupId)){
                group = tempGroup;
                break;
            }
        }
        deleteGroupPopup.show();
        TextView deleteGroupConfirmationText = deleteGroupPopup.findViewById(R.id.deleteGroupConfirmationText);
        AppCompatButton deleteGroupConfirmationButton = deleteGroupPopup.findViewById(R.id.deleteGroupConfirmationButton);
        deleteGroupConfirmationText.setText("Are you sure you want to delete group " + group.getGroupInfo().getGroupName() + " ? This will delete all group data!");
        Group finalGroup = group;
        deleteGroupConfirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteGroup(finalGroup);
                deleteGroupPopup.cancel();
            }
        });
    }

    private void deleteGroup(Group group) {

        usersDatasource.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();
                    for (UserInGroupInfo userInGroup:group.getUsersInGroup()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            User user = dataSnapshot.getValue(User.class);
                            if (userInGroup.getId().equals(user.getId())){
                                users.add(user);
                            }
                        }
                    }
                for (User user:users) {
                    List<String> userGroups = user.getGroups();
                    userGroups.remove(group.getGroupInfo().getId());
                    user.setGroups(userGroups);
                    usersDatasource.child(user.getId()).child("groups").setValue(userGroups);
                }
                storageReference.child(group.getGroupInfo().getId()).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item:listResult.getItems()) {
                            item.delete();
                        }
                    }
                });
                groupsDatasource.child(group.getGroupInfo().getId()).removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserHomeScreen.this,"Connection Error",Toast.LENGTH_LONG).show();
            }
        });

    }

    public void createNewGroup(){
        AppCompatEditText newGroupNameField = newGroupNamePopup.findViewById(R.id.newGroupNameField);
        TextView groupNameError = newGroupNamePopup.findViewById(R.id.groupNameError);
        groupNameError.setVisibility(View.INVISIBLE);
        if (!(Objects.requireNonNull(newGroupNameField.getText()).toString().equals("")) ){
            getLoggedInUserInfo(userInfo.getId());
            Group group = new Group();
            String groupId = groupsDatasource.push().getKey();
            group.setGroupInfo(new GroupInfo(groupId,newGroupNameField.getText().toString(),new Date()));
            List<UserInGroupInfo> usersInGroup = new ArrayList<>();
            usersInGroup.add(new UserInGroupInfo(userInfo.getId(), Role.OWNER));
            group.setUsersInGroup(usersInGroup);
            List<String> userGroups = new ArrayList<>();
            if (userInfo.getGroups()!=null){
                userGroups = userInfo.getGroups();
            }
            userGroups.add(group.getGroupInfo().getId());
            userInfo.setGroups(userGroups);
            usersDatasource.child(userInfo.getId()).setValue(userInfo);
            groupsDatasource.child(groupId).setValue(group);
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
                currentUserGroups.clear();
                for (DataSnapshot data: snapshot.getChildren()) {
                    if (userInfo.getGroups()!=null){
                        for (String userGroup:userInfo.getGroups()) {
                            if (userGroup.equals(data.getKey())) {
                                Group group = new Group();
                                List<Event> events = new ArrayList<>();
                                for (DataSnapshot eventsData:data.child("events").getChildren()) {
                                    events.add(eventsData.getValue(Event.class));
                                }
                                group.setEvents(events);
                                group.setGroupInfo(data.child("groupInfo").getValue(GroupInfo.class));
                                List<UserInGroupInfo> usersInGroup = new ArrayList<>();
                                for (DataSnapshot userInGroupData:data.child("usersInGroup").getChildren()) {
                                    usersInGroup.add(userInGroupData.getValue(UserInGroupInfo.class));
                                }
                                group.setUsersInGroup(usersInGroup);
                                currentUserGroups.add(group);
                            }
                        }
                    }
                }
                userGroupRecycleViewAdapter.updateGroupsList(currentUserGroups);
                groupsSetOnClickListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserHomeScreen.this,error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void groupsSetOnClickListener(){
        userGroupRecycleViewAdapter.setOnItemClickListener(new UserGroupRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String groupId) {
                if (deleteGroupMode){
                    showDeleteGroupPopup(groupId);
                }
                else {
                    Intent inGroupScreenIntent = new Intent(UserHomeScreen.this,InGroupScreen.class);
                    inGroupScreenIntent.putExtra("groupId",groupId);
                    startActivity(inGroupScreenIntent);
                }
            }
        });
    }

    public void instantiate(){
        mAuth = FirebaseAuth.getInstance();
        userAuth = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://tourismclubmanagement-default-rtdb.europe-west1.firebasedatabase.app/");
        usersDatasource = database.getReference("users");
        groupsDatasource = database.getReference("groups");
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        currentUserGroups = new ArrayList<>();
        deleteGroupPopup = new Dialog(UserHomeScreen.this);
        deleteGroupMode = false;
        generateButtons();
    }
    public void getLoggedInUserInfo(String userId){
        usersDatasource.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userInfo = dataSnapshot.getValue(User.class);
                TextView userEmailField = findViewById(R.id.userEmailField);
                userEmailField.setText(userInfo.getEmail());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserHomeScreen.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}