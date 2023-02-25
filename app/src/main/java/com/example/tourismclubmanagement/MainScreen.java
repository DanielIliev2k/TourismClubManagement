package com.example.tourismclubmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.tourismclubmanagement.adapters.MyPagerAdapter;
import com.example.tourismclubmanagement.models.Event;
import com.example.tourismclubmanagement.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class MainScreen extends AppCompatActivity {

    private AppCompatEditText eventNameAdd;
    private AppCompatEditText locationAdd;
    private AppCompatEditText durationAdd;
    private AppCompatEditText equipmentAdd;
    private AppCompatEditText notesAdd;
    private Intent loginIntent;
    private TimePicker timePickerAdd;
    private DatePicker datePickerAdd;
    private User loggedInUser;
    private Dialog addEventPopup;
    private MyPagerAdapter adapter;
    private ViewPager viewPager;
    private AppCompatButton button;
    private Event event;
    private ArrayList<Event> eventsList;
    private FirebaseDatabase database;
    private DatabaseReference eventsDatasource;
    private LayoutInflater pages_inflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        loginIntent = this.getIntent();
        loggedInUser = (User)loginIntent.getExtras().getSerializable("user");
        //Setting connection to the Firebase database
        database = FirebaseDatabase.getInstance("https://tourismclubmanagement-default-rtdb.europe-west1.firebasedatabase.app/");
        eventsDatasource = database.getReference("groups").child(loggedInUser.getGroupId()).child("events");
        addEventPopup = new Dialog(MainScreen.this);
        //Instantiating the list in which the events from database will be stored
        eventsList = new ArrayList<>();
        button = findViewById(R.id.addEventButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddEventPopup(v);
            }
        });
        //Getting the events from the database
        eventsDatasource.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    event = snapshot.getValue(Event.class);
                    eventsList.add(event);
                }
                //Instantiating the main pages of the application after the events data has been pulled from database
                generatePages();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainScreen.this, "Failed to retrieve data.", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void generatePages(){
        pages_inflater = LayoutInflater.from(MainScreen.this);
        adapter = new MyPagerAdapter(pages_inflater,eventsList);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);

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
        addEventPopup.dismiss();
        addEventPopup=new Dialog(MainScreen.this);

    }
}