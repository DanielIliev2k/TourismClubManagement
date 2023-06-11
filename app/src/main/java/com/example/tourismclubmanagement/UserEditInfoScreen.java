package com.example.tourismclubmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tourismclubmanagement.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserEditInfoScreen extends AppCompatActivity {
    private Intent mainScreenIntent;
    private FirebaseUser userAuth;
    private User userInfo;
    private AppCompatButton saveUserInfoButton;
    private TextInputEditText userOwnNameField;
    private TextInputEditText userAgeField;
    private TextView currentUserEmail;
    private TextInputEditText userHometownField;
    private FirebaseDatabase database;
    private DatabaseReference usersDatasource;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_info_screen);
        instantiate();
        setOnClickListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (userAuth!=null){
            getUserInfoFromDb(userAuth.getUid());
        }
    }

    public void getInfoFromFields(){
        userInfo.setName(userOwnNameField.getText().toString());
        userInfo.setAge(Integer.parseInt(userAgeField.getText().toString()));
        userInfo.setHometown(userHometownField.getText().toString());
    }
    public void instantiate(){
        mAuth = FirebaseAuth.getInstance();
        userAuth = mAuth.getCurrentUser();
        currentUserEmail= findViewById(R.id.currentUserEmail);
        saveUserInfoButton = findViewById(R.id.saveUserInfoAndLoginButton);
        userHometownField = findViewById(R.id.hometownField);
        userOwnNameField = findViewById(R.id.nameField);
        userAgeField = findViewById(R.id.ageField);
        database = FirebaseDatabase.getInstance("https://tourismclubmanagement-default-rtdb.europe-west1.firebasedatabase.app/");
        usersDatasource = database.getReference("users");
        generateButtons();
    }
    public void generateButtons(){
        AppCompatButton logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent loginIntent = new Intent(UserEditInfoScreen.this,LoginScreen.class);
                startActivity(loginIntent);
            }
        });
    }
    public void getUserInfoFromDb(String userId){
        usersDatasource.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(userId)){
                        userInfo = snapshot.getValue(User.class);
                        if (!userInfo.getFirstLogin()){
                            populateFields();
                        }
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserEditInfoScreen.this, "Failed to retrieve data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void populateFields(){
        userOwnNameField.setText(userInfo.getName());
        userAgeField.setText(userInfo.getAge().toString());
        userHometownField.setText(userInfo.getHometown());
        currentUserEmail.setText(userInfo.getEmail());
    }
    public void setOnClickListeners(){
        saveUserInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfoFromFields();
                if (userInfo.getFirstLogin()){
                    userInfo.setFirstLogin(false);
                    pushUserToDb();
                    login();
                }
                else {
                    pushUserToDb();
                }
            }
        });
    }
    public void pushUserToDb(){
        usersDatasource.child(userInfo.getId()).setValue(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(UserEditInfoScreen.this,"Saved!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(UserEditInfoScreen.this,"Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void login(){
        mainScreenIntent = new Intent(this, UserHomeScreen.class);
        startActivity(mainScreenIntent);
    }
}