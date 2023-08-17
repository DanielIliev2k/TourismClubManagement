package com.example.tourismclubmanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tourismclubmanagement.comparators.ImageComparator;
import com.example.tourismclubmanagement.listeners.GroupListener;
import com.example.tourismclubmanagement.models.Image;
import com.example.tourismclubmanagement.repositories.GroupRepository;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

public class GroupEditInfoScreen extends AppCompatActivity {
    private GroupRepository groupRepository;
    private String groupId;
    private String groupName;
    private AppCompatEditText groupNameField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit_info_screen);
        groupRepository = new GroupRepository();
        groupId = getIntent().getStringExtra("groupId");
        assert groupId != null;
        setButtonOnClickListeners();
        displayGroupName();
    }
    public void displayGroupName(){
        groupNameField = findViewById(R.id.groupNameField);
        TextView currentGroupName = findViewById(R.id.currentGroupName);
        groupRepository.getGroupName(groupId, new GroupListener() {
            @Override
            public void onGroupNameLoaded(String name) {
                currentGroupName.setText(name);
                groupNameField.setText(name);
                groupName = name;
            }

            @Override
            public void onFailure(DatabaseError error) {
                Toast.makeText(GroupEditInfoScreen.this,"Database Error!",Toast.LENGTH_LONG).show();
            }
        });
    }
    public void setButtonOnClickListeners(){
        AppCompatButton deleteGroupButton = findViewById(R.id.deleteGroupButton);
        AppCompatButton saveNewNameButton = findViewById(R.id.saveNewNameButton);
        AppCompatButton homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(GroupEditInfoScreen.this,UserHomeScreen.class);
                startActivity(homeIntent);
            }
        });
        saveNewNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newGroupName = groupNameField.getText().toString();
                groupRepository.updateGroupName(groupId,newGroupName);
                Toast.makeText(GroupEditInfoScreen.this,"Name updated!",Toast.LENGTH_SHORT).show();
                Intent inGroupIntent = new Intent(GroupEditInfoScreen.this,InGroupScreen.class);
                inGroupIntent.putExtra("groupId",groupId);
                startActivity(inGroupIntent);
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
        Dialog deleteConfirmationPopup = new Dialog(GroupEditInfoScreen.this);
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
                groupRepository.deleteGroup(groupId, new GroupListener() {
                    @Override
                    public void onGroupDeleted(String response) {
                        Intent homeIntent = new Intent(GroupEditInfoScreen.this,UserHomeScreen.class);
                        startActivity(homeIntent);
                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                        StorageReference imagesReference = firebaseStorage.getReference().child(groupId);
                        imagesReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                            @Override
                            public void onSuccess(ListResult listResult) {
                                if (!listResult.getItems().isEmpty()) {
                                    for (StorageReference file : listResult.getItems()) {
                                        imagesReference.child(file.getName()).delete();
                                    }
                                }
                            }
                        });
                    }
                });

            }
        });
    }
}