package com.example.tourismclubmanagement;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tourismclubmanagement.models.User;
import com.example.tourismclubmanagement.models.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginScreen extends AppCompatActivity {

    private DatabaseReference usersDatasource;
    private FirebaseAuth mAuth;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://tourismclubmanagement-default-rtdb.europe-west1.firebasedatabase.app/");
        usersDatasource = database.getReference("users");
        mAuth = FirebaseAuth.getInstance();
        setButtonOnClickListeners();
    }
    public void setButtonOnClickListeners(){
        AppCompatButton loginButton = findViewById(R.id.loginButton);
        AppCompatButton registerButton = findViewById(R.id.registerButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              login();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }
    private void register() {
        Intent registerIntent = new Intent(LoginScreen.this, RegisterScreen.class);
        startActivity(registerIntent);
    }

    public void login(){
        TextInputEditText usernameField = findViewById(R.id.usernameField);
        TextInputEditText passwordField = findViewById(R.id.passwordField);
        TextView loginUsernameError = findViewById(R.id.loginUsernameError);
        TextView loginPasswordError = findViewById(R.id.loginPasswordError);
        loginUsernameError.setVisibility(View.INVISIBLE);
        loginPasswordError.setVisibility(View.INVISIBLE);
        if (!(Objects.requireNonNull(usernameField.getText()).toString().equals("")) &&!Objects.requireNonNull(passwordField.getText()).toString().equals("")){
            mAuth.signInWithEmailAndPassword(usernameField.getText().toString(), passwordField.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser userAuth = mAuth.getCurrentUser();
                                getUserInfoFromDb(userAuth.getUid());

                            } else {
                                Toast.makeText(LoginScreen.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        if ((Objects.requireNonNull(usernameField.getText()).toString().equals(""))) {
            loginUsernameError.setText("Please type your email");
            loginUsernameError.setVisibility(View.VISIBLE);
        }
        if ((Objects.requireNonNull(passwordField.getText()).toString().equals(""))) {
            loginPasswordError.setText("Please type your password");
            loginPasswordError.setVisibility(View.VISIBLE);
        }
    }
    public void getUserInfoFromDb(String userId){
        usersDatasource.child(userId).child("userInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInfo = snapshot.getValue(UserInfo.class);
                Intent loginIntent = new Intent(LoginScreen.this, UserHomeScreen.class);
                if (userInfo.getFirstLogin()){
                    loginIntent = new Intent(LoginScreen.this, UserEditInfoScreen.class);
                }
                startActivity(loginIntent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginScreen.this,"User error!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}