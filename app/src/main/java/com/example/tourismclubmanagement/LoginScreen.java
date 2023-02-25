package com.example.tourismclubmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.tourismclubmanagement.models.Event;
import com.example.tourismclubmanagement.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoginScreen extends AppCompatActivity {

    private TextInputEditText usernameField;
    private TextInputEditText passwordField;
    private AppCompatButton loginButton;
    private AppCompatButton createNewGroupButton;
    private Intent createGroupIntent;
    private Intent mainScreenIntent;
    private User user;
    private List<User> users;
    private FirebaseDatabase database;
    private DatabaseReference usersDatasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        database = FirebaseDatabase.getInstance("https://tourismclubmanagement-default-rtdb.europe-west1.firebasedatabase.app/");
        usersDatasource = database.getReference("users");
        instantiate();
        setButtonOnClickListeners();
    }
    public void setButtonOnClickListeners(){
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        createNewGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGroupCreation();
            }
        });
    }
    public void newGroupCreation(){
        startActivity(createGroupIntent);
    }
    public void instantiate(){
        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        createNewGroupButton = findViewById(R.id.createNewGroupButton);
        createGroupIntent = new Intent(this,CreateNewGroupScreen.class);
        mainScreenIntent = new Intent(this,MainScreen.class);
        user = new User();
        users = new ArrayList<>();
        users = getUserInfoFromDb();
    }
    public void login(){
        if (checkUserCredentials()){
            mainScreenIntent.putExtra("user",user);
            this.startActivity(mainScreenIntent);
        }
        else{
            Toast.makeText(this,"Error",Toast.LENGTH_LONG);
        }
    }
    public List<User> getUserInfoFromDb(){
        usersDatasource.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                User user = new User();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    user = snapshot.getValue(User.class);
                    users.add(user);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginScreen.this, "Failed to retrieve data.", Toast.LENGTH_SHORT).show();
            }
        });
        return users;
    }
    public Boolean checkUserCredentials(){
        user.setUsername(usernameField.getText().toString());
        user.setPassword(passwordField.getText().toString());
        if (users.isEmpty()){
            return false;
        }
        for (User tempUser:users) {
            if (user.getUsername().equals(user.getUsername()) && user.getPassword().equals(tempUser.getPassword())){
                user = tempUser;
                return true;
            }
        }
        return false;
    }
}