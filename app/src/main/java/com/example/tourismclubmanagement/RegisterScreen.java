package com.example.tourismclubmanagement;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterScreen extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        instantiate();
    }
    public void instantiate(){
        firebaseDatabase = FirebaseDatabase.getInstance("https://tourismclubmanagement-default-rtdb.europe-west1.firebasedatabase.app/");
        usersReference = firebaseDatabase.getReference("users");
        AppCompatButton registerSubmitButton = findViewById(R.id.registerSubmitButton);
        registerSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRegistrationInfo();
            }
        });
    }

    private void submitRegistrationInfo() {
        TextInputEditText registerUsernameField = findViewById(R.id.registerUsernameField);
        TextInputEditText registerPasswordField = findViewById(R.id.registerPasswordField);
        TextView registerUsernameError = findViewById(R.id.registerUsernameError);
        TextView registerPasswordError = findViewById(R.id.registerPasswordError);
        registerUsernameError.setVisibility(View.INVISIBLE);
        registerPasswordError.setVisibility(View.INVISIBLE);
        if (!(Objects.requireNonNull(registerUsernameField.getText()).toString().equals("")) &&!Objects.requireNonNull(registerPasswordField.getText()).toString().equals("")){
            mAuth.createUserWithEmailAndPassword(registerUsernameField.getText().toString(), registerPasswordField.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser userAuth = mAuth.getCurrentUser();
                            User user = new User();
                            UserInfo userInfo = new UserInfo();
                            userInfo.setId(userAuth.getUid());
                            userInfo.setEmail(userAuth.getEmail());
                            userInfo.setFirstLogin(true);
                            user.setUserInfo(userInfo);
                            usersReference.child(user.getUserInfo().getId()).setValue(user);
                            Toast.makeText(RegisterScreen.this, userAuth.getEmail(),
                                    Toast.LENGTH_LONG).show();
                            Intent firstLoginScreen = new Intent(RegisterScreen.this, UserEditInfoScreen.class);
                            startActivity(firstLoginScreen);
                        } else {
                            Toast.makeText(RegisterScreen.this, Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        }
        if ((Objects.requireNonNull(registerUsernameField.getText()).toString().equals(""))) {
            registerUsernameError.setText("Please type your email");
            registerUsernameError.setVisibility(View.VISIBLE);
        }
        if ((Objects.requireNonNull(registerPasswordField.getText()).toString().equals(""))) {
            registerPasswordError.setText("Please type your password");
            registerPasswordError.setVisibility(View.VISIBLE);
        }

    }
}