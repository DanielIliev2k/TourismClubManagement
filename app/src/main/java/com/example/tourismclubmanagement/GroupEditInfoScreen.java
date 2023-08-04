package com.example.tourismclubmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tourismclubmanagement.models.Group;
import com.example.tourismclubmanagement.models.GroupInfo;
import com.example.tourismclubmanagement.models.User;
import com.example.tourismclubmanagement.models.UserGroup;
import com.example.tourismclubmanagement.models.UserInGroupInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class GroupEditInfoScreen extends AppCompatActivity {
    private DatabaseReference groupDatasource;
    private DatabaseReference usersDatasource;
    private Dialog deleteConfirmationPopup;
    private String groupId;
    private String groupName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit_info_screen);
        groupId = getIntent().getStringExtra("groupId");
        assert groupId != null;
        groupDatasource = FirebaseDatabase.getInstance("https://tourismclubmanagement-default-rtdb.europe-west1.firebasedatabase.app/").getReference("groups").child(groupId);
        usersDatasource = FirebaseDatabase.getInstance("https://tourismclubmanagement-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");
        setButtonOnClickListeners();
        displayGroupName();
    }
    public void displayGroupName(){
        TextView currentGroupName = findViewById(R.id.currentGroupName);
        groupDatasource.child("groupInfo").child("groupName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupName = snapshot.getValue(String.class);
                currentGroupName.setText(groupName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void setButtonOnClickListeners(){
        AppCompatButton deleteGroupButton = findViewById(R.id.deleteGroupButton);
        AppCompatButton saveGroupInfoButton = findViewById(R.id.saveGroupInfoButton);
        saveGroupInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveGroupInfo();
            }
        });
        deleteGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationPopup();
            }
        });
    }

    private void showDeleteConfirmationPopup() {
        deleteConfirmationPopup = new Dialog(GroupEditInfoScreen.this);
        deleteConfirmationPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deleteConfirmationPopup.setCancelable(true);
        deleteConfirmationPopup.setContentView(R.layout.confirm_group_delete_popup);
        deleteConfirmationPopup.show();
        AppCompatButton deleteGroupConfirmationButton = deleteConfirmationPopup.findViewById(R.id.deleteGroupConfirmationButton);
        TextView deleteGroupConfirmationText = deleteConfirmationPopup.findViewById(R.id.deleteGroupConfirmationText);
        deleteGroupConfirmationText.setText("Are you sure you want to delete this group: " + groupName + "?");
        deleteGroupConfirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteGroup();
            }
        });
    }

    private void deleteGroup() {
        groupDatasource.child("usersInGroup").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    UserInGroupInfo userInGroup = dataSnapshot.getValue(UserInGroupInfo.class);
                    usersDatasource.child(userInGroup.getId()).child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<UserGroup> userGroups = new ArrayList<>();
                            for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                                UserGroup userGroup = dataSnapshot.getValue(UserGroup.class);
                                assert userGroup != null;
                                if (!userGroup.getGroupId().equals(groupId)){
                                    userGroups.add(userGroup);
                                }
                            }
                            usersDatasource.child(userInGroup.getId()).child("groups").setValue(userGroups);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                groupDatasource.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent userHomeScreenIntent = new Intent(GroupEditInfoScreen.this,UserHomeScreen.class);
                        startActivity(userHomeScreenIntent);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveGroupInfo() {
        AppCompatEditText groupNameField = findViewById(R.id.groupNameField);
        String newGroupName = groupNameField.getText().toString();
        groupDatasource.child("groupInfo").child("groupName").setValue(newGroupName);

    }
}