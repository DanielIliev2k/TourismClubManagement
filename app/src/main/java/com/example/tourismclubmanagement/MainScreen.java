package com.example.tourismclubmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.DialogInterface;
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

    private AppCompatEditText eventNameField;
    private AppCompatEditText eventLocationField;
    private AppCompatEditText eventDurationField;
    private AppCompatEditText eventEquipmentField;
    private AppCompatEditText eventNotesField;
    private TimePicker eventTimePicker;
    private DatePicker eventDatePicker;
    private Intent loginIntent;
    private AppCompatButton deleteEventButton;
    private User loggedInUser;
    private Dialog eventFormPopup;
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
    private Boolean editEventMode;
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
        eventFormPopup = new Dialog(MainScreen.this);
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
        generateEditEventButton();
        generateDeleteEventButton();
        initialDataPullsComplete = true;

    }
    public void showEventFormPopup(Event event){
        eventFormPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        eventFormPopup.setCancelable(true);
        eventFormPopup.setContentView(R.layout.event_form_popup);
        eventFormPopup.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                eventFormPopup = new Dialog(MainScreen.this);
            }
        });

        eventFormPopup.show();

        instantiateEventFormElements();
        if (editEventMode){
            populateEventFields(event);
        }
        final AppCompatButton saveEventButton = eventFormPopup.findViewById(R.id.saveEventButton);
        saveEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFormEventToDb(event);
            }
        });
    }
    public void populateEventFields(Event event){
        eventNameField.setText(event.getEventName());
        eventLocationField.setText(event.getLocation());
        eventDurationField.setText(event.getDuration());
        eventEquipmentField.setText(event.getEquipment());
        eventNotesField.setText(event.getNotes());
        eventDatePicker.updateDate(event.getDepartureTime().getYear(),event.getDepartureTime().getMonth(),event.getDepartureTime().getDay());
        eventTimePicker.setHour(event.getDepartureTime().getHours());
        eventTimePicker.setMinute(event.getDepartureTime().getMinutes());
    }
    public void instantiateEventFormElements(){
        eventNameField = eventFormPopup.findViewById(R.id.eventNameField);
        eventLocationField = eventFormPopup.findViewById(R.id.eventLocationField);
        eventDurationField = eventFormPopup.findViewById(R.id.eventDurationField);
        eventEquipmentField = eventFormPopup.findViewById(R.id.eventEquipmentField);
        eventNotesField = eventFormPopup.findViewById(R.id.eventNotesField);
        eventDatePicker = eventFormPopup.findViewById(R.id.eventDatePicker);
        eventTimePicker = eventFormPopup.findViewById(R.id.eventTimePicker);
    }
    public void addFormEventToDb(Event event){
        String eventId = new String();
        final Date eventTime = new Date();
        eventTime.setHours(eventTimePicker.getHour());
        eventTime.setMinutes(eventTimePicker.getMinute());
        eventTime.setDate(eventDatePicker.getDayOfMonth());
        eventTime.setMonth(eventDatePicker.getMonth());
        eventTime.setYear(eventDatePicker.getYear());
        event.setEventName(eventNameField.getText().toString());
        event.setDuration(eventDurationField.getText().toString());
        event.setLocation(eventLocationField.getText().toString());
        event.setEquipment(eventEquipmentField.getText().toString());
        event.setNotes(eventNotesField.getText().toString());
        event.setEventName(eventNameField.getText().toString());
        event.setDepartureTime(eventTime);
        if (event.getId()!=null){
            eventId = event.getId();
        }
        else{
            eventId = eventsDatasource.push().getKey();
            event.setId(eventId);
        }
        eventsDatasource.child(eventId).setValue(event);
        updateEventsFromDb();
        eventFormPopup.dismiss();
        eventFormPopup =new Dialog(MainScreen.this);

    }
    public void updateEventsFromDb(){
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
    }
    public void instantiate(){
        eventsList = new ArrayList<>();
        usersList = new ArrayList<>();
        usersInGroupList = new ArrayList<>();
        takenUsersFromDb =false;
        takenEventsFromDb =false;
        takenGroupFromDb =false;
        deleteEventMode = false;
        editEventMode = false;
        initialDataPullsComplete = false;
    }
    public void generateAddEventButton(){

            AppCompatButton addEventButton = pagerAdapter.getPage(0).findViewById(R.id.addEventButton);
            addEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEventFormPopup(new Event());
                    deleteEventMode = false;
                    editEventMode = false;
                }
            });
    }
    public void generateEditEventButton(){

        AppCompatButton editEventButton = pagerAdapter.getPage(0).findViewById(R.id.editEventButton);
        editEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editEventMode = !editEventMode;
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
                if (editEventMode){
                    Event event = new Event();
                    for (Event tempEvent:eventsList) {
                        if (tempEvent.getId().equals(eventId)){
                            event = tempEvent;
                            break;
                        }
                    }
                    showEventFormPopup(event);
                }
            }
        });
    }
}