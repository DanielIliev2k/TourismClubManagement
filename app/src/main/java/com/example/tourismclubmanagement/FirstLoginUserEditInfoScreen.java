package com.example.tourismclubmanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.example.tourismclubmanagement.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class FirstLoginUserEditInfoScreen extends AppCompatActivity {
    private Intent mainScreenIntent;
    private Intent loginIntent;
    private User user;
    private AppCompatButton saveAndLoginButton;
    private TextInputEditText userOwnNameField;
    private TextInputEditText userAgeField;
    private TextInputEditText userHometownField;
    private FirebaseDatabase database;
    private DatabaseReference usersDatasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login_user_edit_info_screen);
        getUserFromIntent();
        instantiate();
        setOnClickListeners();
    }
    public void getInfoFromFields(){
        user.setName(userOwnNameField.getText().toString());
        user.setAge(Integer.parseInt(userAgeField.getText().toString()));
        user.setHometown(userHometownField.getText().toString());
        user.setFirstLogin(false);
    }
    public void instantiate(){
        saveAndLoginButton = findViewById(R.id.saveUserInfoAndLoginButton);
        userHometownField = findViewById(R.id.hometownField);
        userOwnNameField = findViewById(R.id.nameField);
        userAgeField = findViewById(R.id.ageField);
        database = FirebaseDatabase.getInstance("https://tourismclubmanagement-default-rtdb.europe-west1.firebasedatabase.app/");
        usersDatasource = database.getReference("users");
    }
    public void setOnClickListeners(){
        saveAndLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfoFromFields();
                pushUserToDb();
                login();
            }
        });
    }
    public void pushUserToDb(){
        usersDatasource.child(user.getId()).setValue(user);
    }
    public void login(){
        mainScreenIntent = new Intent(this,MainScreen.class);
        mainScreenIntent.putExtra("user",user);
        startActivity(mainScreenIntent);
    }
    public void getUserFromIntent(){
        loginIntent = this.getIntent();
        user = (User)loginIntent.getExtras().getSerializable("user");
    }
}