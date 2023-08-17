package com.example.tourismclubmanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tourismclubmanagement.adapters.UserGroupRecyclerViewAdapter;
import com.example.tourismclubmanagement.comparators.UserGroupComparator;
import com.example.tourismclubmanagement.listeners.GroupListener;
import com.example.tourismclubmanagement.listeners.UserListener;
import com.example.tourismclubmanagement.models.User;
import com.example.tourismclubmanagement.repositories.GroupRepository;
import com.example.tourismclubmanagement.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;

import java.util.List;
import java.util.Objects;

public class UserHomeScreen extends AppCompatActivity {
    private FirebaseUser userAuth;
    private  FirebaseAuth mAuth;
    private User user;
    private Dialog newGroupNamePopup;
    private UserGroupRecyclerViewAdapter userGroupRecycleViewAdapter;
    private UserRepository userRepository;
    private GroupRepository groupRepository;
    private Boolean firstUserPull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_screen);
        instantiate();
        getLoggedInUserInfo(userAuth.getUid());
    }

    public void displayUserGroups() {
        RecyclerView userGroupsContainer = findViewById(R.id.userGroupsContainer);
        userGroupsContainer.setLayoutManager(new LinearLayoutManager(this));
        userGroupRecycleViewAdapter = new UserGroupRecyclerViewAdapter( user,userRepository,groupRepository);
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
                showSettingsPopupMenu(v);
            }
        });
        createNewGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateGroupPopup();
            }
        });

    }
    private void showSettingsPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.settings_menu, popupMenu.getMenu());



        popupMenu.getMenu().removeItem(R.id.settingsGroupButton);
        popupMenu.getMenu().removeItem(R.id.settingsLeaveGroup);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.settingsUserButton:
                        Intent userEditInfoIntent = new Intent(UserHomeScreen.this,UserEditInfoScreen.class);
                        startActivity(userEditInfoIntent);
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }
    public void createNewGroup(){
        AppCompatEditText newGroupNameField = newGroupNamePopup.findViewById(R.id.newGroupNameField);
        TextView groupNameError = newGroupNamePopup.findViewById(R.id.groupNameError);
        groupNameError.setVisibility(View.INVISIBLE);
        if ((Objects.requireNonNull(newGroupNameField.getText()).toString().equals(""))) {
            groupNameError.setText("Please type a name for the group");
            groupNameError.setVisibility(View.VISIBLE);
        }
        else {
            groupRepository.getAllGroupNamesOnce(new GroupListener() {
                @Override
                public void onAllGroupNamesLoaded(List<String> groupNames) {
                    if (!groupNames.contains(newGroupNameField.getText().toString())){
                        groupRepository.addGroup(user.getUserInfo().getId(),newGroupNameField.getText().toString());
                        newGroupNamePopup.cancel();
                    }
                    else {
                        Toast.makeText(UserHomeScreen.this,"Group with that name already exists!",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(DatabaseError error) {
                    Toast.makeText(UserHomeScreen.this,"Database error!",Toast.LENGTH_SHORT).show();
                }
            });
        }
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
        groupRepository = new GroupRepository();
        userRepository = new UserRepository();
        firstUserPull = true;
        generateButtons();
    }
    public void getLoggedInUserInfo(String userId){
        TextView userEmailField = findViewById(R.id.userEmailField);
        userRepository.getUser(userId, new UserListener() {
            @Override
            public void onUserLoaded(User user) {
                if (firstUserPull){
                    UserHomeScreen.this.user = user;
                    userEmailField.setText(user.getUserInfo().getEmail());
                    displayUserGroups();
                    firstUserPull = false;
                    groupsSetOnClickListener();
                }
                user.getGroups().sort(new UserGroupComparator());
                userGroupRecycleViewAdapter.updateUser(user);
            }

            @Override
            public void onFailure(DatabaseError error) {
                Toast.makeText(UserHomeScreen.this,"Database Error!",Toast.LENGTH_LONG).show();
            }
        });
    }


}