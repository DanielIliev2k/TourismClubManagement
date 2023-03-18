package com.example.tourismclubmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.tourismclubmanagement.adapters.EventRecyclerViewAdapter;
import com.example.tourismclubmanagement.adapters.MyPagerAdapter;
import com.example.tourismclubmanagement.models.Event;
import com.example.tourismclubmanagement.models.Group;
import com.example.tourismclubmanagement.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class MainScreen extends AppCompatActivity implements EventRecyclerViewAdapter.OnItemClickListener {

    private AppCompatEditText eventNameAdd;
    private AppCompatEditText locationAdd;
    private AppCompatEditText durationAdd;
    private AppCompatEditText equipmentAdd;
    private AppCompatEditText notesAdd;
    private Intent loginIntent;
    private AppCompatButton deleteEventButton;
    private TimePicker timePickerAdd;
    private DatePicker datePickerAdd;
    private User loggedInUser;
    private Dialog addEventPopup;
    private Dialog newUserInfoPopup;
    private Dialog deleteEventPopup;
    private MyPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private Group group;
    private ArrayList<Event> eventsList;
    private ArrayList<User> usersList;
    private ArrayList<String> usersInGroupList;
    private FirebaseDatabase database;
    private DatabaseReference eventsDatasource;
    private DatabaseReference usersDatasource;
    private DatabaseReference groupsDatasource;
    private LayoutInflater pages_inflater;
    private Boolean takenUsersFromDb;

    private Boolean takenEventsFromDb;
    private Boolean takenGroupFromDb;
    private RecyclerView eventsRecyclerView;
    private Boolean deleteEventMode;
    private Boolean initialDataPullsComplete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        loginIntent = this.getIntent();
        loggedInUser = (User)loginIntent.getExtras().getSerializable("user");
        //Setting connection to the Firebase database
        database = FirebaseDatabase.getInstance("https://tourismclubmanagement-default-rtdb.europe-west1.firebasedatabase.app/");
        eventsDatasource = database.getReference("groups").child(loggedInUser.getGroupId()).child("events");
        usersDatasource = database.getReference("users");
        groupsDatasource = database.getReference("groups");
        addEventPopup = new Dialog(MainScreen.this);
        newUserInfoPopup = new Dialog(MainScreen.this);
        instantiate();

        getGroupFromDb();

    }
    @Override
    public void onItemClick(String eventId) {

    }
    public void getGroupFromDb(){
        groupsDatasource.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                group = new Group();
                ArrayList<Event> events = new ArrayList<>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(loggedInUser.getGroupId())){
                        for (DataSnapshot event:snapshot.child("events").getChildren()) {
                            events.add(event.getValue(Event.class));
                        }
                        group = snapshot.getValue(Group.class);
                    }
                }
                takenGroupFromDb = true;
                usersInGroupList = group.getUserIds();
                getUsersFromDb();
                getEventsFromDb();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainScreen.this, "Failed to retrieve data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void getEventsFromDb(){
        eventsDatasource.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventsList.clear();
                Event event = new Event();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    event = snapshot.getValue(Event.class);
                    eventsList.add(event);
                }
                takenEventsFromDb = true;
                checkIfDatabasePullsReady();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainScreen.this, "Failed to retrieve data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void getUsersFromDb(){
        usersDatasource.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                User tempUser;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (usersInGroupList.contains(snapshot.getKey())){
                        tempUser = snapshot.getValue(User.class);
                        if (loggedInUser.getAdmin()){
                            usersList.add(tempUser);
                        }
                        else{
                            if (!tempUser.getFirstLogin()) {
                                usersList.add(tempUser);
                            }
                        }

                    }
                }
                takenUsersFromDb = true;
                checkIfDatabasePullsReady();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainScreen.this, "Failed to retrieve data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void checkIfDatabasePullsReady(){
        if (!initialDataPullsComplete){
            if (takenUsersFromDb&&takenEventsFromDb&&takenGroupFromDb) {
                //Instantiating the main pages of the application after the events data has been pulled from database
                generatePages();
                takenGroupFromDb = false;
                takenEventsFromDb = false;
                takenGroupFromDb = false;
            }
        }
    }
    public void generatePages(){
        pages_inflater = LayoutInflater.from(MainScreen.this);
        pagerAdapter = new MyPagerAdapter(pages_inflater,eventsList,usersList);
        viewPager = findViewById(R.id.view_pager);
        eventsSetOnItemClickListener();
        viewPager.setAdapter(pagerAdapter);
        generateAddEventButton();
        generateAddUserButton();
        generateDeleteEventButton();
        initialDataPullsComplete = true;

    }
    public void showAddEventPopup(View view){
        addEventPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addEventPopup.setCancelable(true);
        addEventPopup.setContentView(R.layout.add_event_popup);

        addEventPopup.show();

        instantiateAddEventElements();
        final AppCompatButton addEventButton = addEventPopup.findViewById(R.id.addEventButton);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewEventToDb(view);
            }
        });
    }
    public void instantiateAddEventElements(){
        eventNameAdd = addEventPopup.findViewById(R.id.eventNameAdd);
        locationAdd = addEventPopup.findViewById(R.id.locationAdd);
        durationAdd = addEventPopup.findViewById(R.id.durationAdd);
        equipmentAdd = addEventPopup.findViewById(R.id.equipmentAdd);
        notesAdd = addEventPopup.findViewById(R.id.notesAdd);
        datePickerAdd = addEventPopup.findViewById(R.id.datePickerAdd);
        timePickerAdd = addEventPopup.findViewById(R.id.timePickerAdd);
    }
    public void addNewEventToDb(View view){
        Event event = new Event();
        final Date eventTime = new Date();
        eventTime.setHours(timePickerAdd.getHour());
        eventTime.setMinutes(timePickerAdd.getMinute());
        eventTime.setDate(datePickerAdd.getDayOfMonth());
        eventTime.setMonth(datePickerAdd.getMonth());
        eventTime.setYear(datePickerAdd.getYear());
        event.setEventName(eventNameAdd.getText().toString());
        event.setDuration(durationAdd.getText().toString());
        event.setLocation(locationAdd.getText().toString());
        event.setEquipment(equipmentAdd.getText().toString());
        event.setNotes(notesAdd.getText().toString());
        event.setEventName(eventNameAdd.getText().toString());
        event.setDepartureTime(eventTime);
        String eventId = eventsDatasource.push().getKey();
        event.setId(eventId);
        eventsDatasource.child(eventId).setValue(event);
        eventsDatasource.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventsList.clear();
                Event event = new Event();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    event = snapshot.getValue(Event.class);
                    eventsList.add(event);
                }
                pagerAdapter.updateEvents(eventsList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainScreen.this, "Failed to retrieve data.", Toast.LENGTH_SHORT).show();
            }
        });
        addEventPopup.dismiss();
        addEventPopup=new Dialog(MainScreen.this);

    }
    public void instantiate(){
        eventsList = new ArrayList<>();
        usersList = new ArrayList<>();
        usersInGroupList = new ArrayList<>();
        takenUsersFromDb =false;
        takenEventsFromDb =false;
        takenGroupFromDb =false;
        deleteEventMode = false;
        initialDataPullsComplete = false;
    }
    public void generateAddEventButton(){

            AppCompatButton addEventButton = pagerAdapter.getPage(0).findViewById(R.id.addEventButton);
            addEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddEventPopup(v);
                    deleteEventMode = false;
                }
            });
    }
    public void generateAddUserButton(){
        AppCompatButton addUserButton = pagerAdapter.getPage(1).findViewById(R.id.addUserButton);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewUserInfoPopup(v);
            }
        });
    }
    public void generateDeleteEventButton(){
        deleteEventButton = pagerAdapter.getPage(0).findViewById(R.id.deleteEventButton);
        deleteEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEventMode=!deleteEventMode;
            }
        });
    }
//
    public void showDeleteEventPopup(View view){
        deleteEventPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deleteEventPopup.setCancelable(true);
        deleteEventPopup.setContentView(R.layout.new_user_info_popup);
    }
    public void deleteEvent(String eventId){
        eventsDatasource.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventsList.clear();
                Event event = new Event();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    event = snapshot.getValue(Event.class);
                    eventsList.add(event);
                }
                for (Event tempEvent:eventsList) {
                    if (event.getId().equals(eventId))
                    {
                        eventsList.remove(event);
                        break;
                    }
                }
                pagerAdapter.updateEvents(eventsList);
                eventsDatasource.child(eventId).removeValue();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainScreen.this, "Failed to retrieve data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void showNewUserInfoPopup(View view){
        newUserInfoPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        newUserInfoPopup.setCancelable(true);
        newUserInfoPopup.setContentView(R.layout.new_user_info_popup);

        User user = generateNewUser();
        TextView generatedCredentialsField = newUserInfoPopup.findViewById(R.id.generatedCredentialsField);
        generatedCredentialsField.setText(user.getUsername() + "\n" + user.getPassword());

        newUserInfoPopup.show();
    }
    public User generateNewUser(){
        String userId = usersDatasource.push().getKey();
        User user = new User(group.getId(),userId);
        user.setUsername(userId);
        user.setPassword(userId);
        user.setAdmin(false);
        usersInGroupList.add(userId);
        group.setUserIds(usersInGroupList);
        groupsDatasource.child(group.getId()).setValue(group);
        usersDatasource.child(userId).setValue(user);
        usersDatasource.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                User tempUser;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (usersInGroupList.contains(snapshot.getKey())){
                        tempUser = snapshot.getValue(User.class);
                        if (loggedInUser.getAdmin()){
                            usersList.add(tempUser);
                        }
                        else{
                            if (!tempUser.getFirstLogin()) {
                                usersList.add(tempUser);
                            }
                        }

                    }
                }
                pagerAdapter.updateUsers(usersList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainScreen.this, "Failed to retrieve data.", Toast.LENGTH_SHORT).show();
            }
        });
        return user;
    }
    private void eventsSetOnItemClickListener() {
        EventRecyclerViewAdapter eventsRecyclerViewAdapter = pagerAdapter.getEventRecyclerViewAdapter();
        eventsRecyclerViewAdapter.setOnItemClickListener(new EventRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String eventId) {
                if (deleteEventMode){
                    deleteEvent(eventId);
                }
            }
        });
    }
}