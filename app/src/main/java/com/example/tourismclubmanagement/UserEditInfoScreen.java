package com.example.tourismclubmanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tourismclubmanagement.listeners.UserListener;
import com.example.tourismclubmanagement.models.User;
import com.example.tourismclubmanagement.models.UserInfo;
import com.example.tourismclubmanagement.repositories.GroupRepository;
import com.example.tourismclubmanagement.repositories.UserRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;

public class UserEditInfoScreen extends AppCompatActivity {
    private UserRepository userRepository;
    private FirebaseUser userAuth;
    private TextInputEditText userOwnNameField;
    private TextInputEditText userAgeField;
    private User currentUser;
    private TextView currentUserEmail;
    private TextInputEditText userHometownField;
    private GroupRepository groupRepository;

    public UserEditInfoScreen() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_info_screen);
        instantiate();
        setButtonOnClickListeners();
    }

    public UserInfo getInfoFromFields(){
        UserInfo userInfo = new UserInfo();
        userInfo.setName(userOwnNameField.getText().toString());
        userInfo.setAge(Integer.parseInt(userAgeField.getText().toString()));
        userInfo.setHometown(userHometownField.getText().toString());
        return userInfo;
    }
    public void instantiate(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userAuth = mAuth.getCurrentUser();
        currentUserEmail= findViewById(R.id.currentUserEmail);
        userHometownField = findViewById(R.id.hometownField);
        userOwnNameField = findViewById(R.id.nameField);
        userAgeField = findViewById(R.id.ageField);
        groupRepository = new GroupRepository();
        userRepository = new UserRepository();
        getUserFromDb();
    }
    public void getUserFromDb(){
        userRepository.getUser(userAuth.getUid(), new UserListener() {
            @Override
            public void onUserLoaded(User user) {
                currentUser = user;
                if (!user.getUserInfo().getFirstLogin()){
                    populateFields(user.getUserInfo());
                }
            }
            @Override
            public void onFailure(DatabaseError error) {
                Toast.makeText(UserEditInfoScreen.this,"Database Error!",Toast.LENGTH_LONG).show();
            }
        });
    }
    public void populateFields(UserInfo userInfo){
        userOwnNameField.setText(userInfo.getName());
        userAgeField.setText(userInfo.getAge().toString());
        userHometownField.setText(userInfo.getHometown());
        currentUserEmail.setText(userInfo.getEmail());
    }
    public void setButtonOnClickListeners(){

        AppCompatButton saveUserInfoButton = findViewById(R.id.saveUserInfoAndLoginButton);
        saveUserInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo userInfo = getInfoFromFields();
                userInfo.setId(currentUser.getUserInfo().getId());
                userInfo.setEmail(currentUser.getUserInfo().getEmail());
                userInfo.setFirstLogin(currentUser.getUserInfo().getFirstLogin());
                if (userInfo.getFirstLogin()){
                    userInfo.setFirstLogin(false);
                    userRepository.updateUser(userInfo);
                    login();
                }
                else {
                    userRepository.updateUser(userInfo);
                }
            }
        });
        AppCompatButton homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(UserEditInfoScreen.this,UserHomeScreen.class);
                startActivity(homeIntent);
            }
        });
    }

    public void login(){
        Intent mainScreenIntent = new Intent(this, UserHomeScreen.class);
        startActivity(mainScreenIntent);
    }
}