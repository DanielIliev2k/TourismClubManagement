package com.example.tourismclubmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tourismclubmanagement.adapters.CustomRecyclerViewAdapter;
import com.example.tourismclubmanagement.adapters.MyPagerAdapter;
import com.example.tourismclubmanagement.models.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.datatype.Duration;

public class MainScreen extends AppCompatActivity {

    private MyPagerAdapter adapter;
    private ViewPager viewPager;
    private View eventsPage;
    private View membersPage;
    private View picturesPage;
    private AppCompatButton button;
    private LinearLayoutManager manager;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private Event event;
    private CustomRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private ArrayList<Event> eventsList;
    private FirebaseDatabase database;
    private DatabaseReference eventsDatasource;
    private LayoutInflater pages_inflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        dateFormat = new SimpleDateFormat("ddMMyyyy", Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        //Setting connection to the Firebase database
        database = FirebaseDatabase.getInstance("https://tourismclubmanagement-default-rtdb.europe-west1.firebasedatabase.app/");
        eventsDatasource = database.getReference("events");
        //Instantiating the list in which the events from database will be stored
        eventsList = new ArrayList<>();
        button = findViewById(R.id.addEventButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewEventToDb(v);
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
    public void addNewEventToDb(View view){
        Event event = new Event("Event1","Turistichesko drujestvo bacho kiro", new Date(System.currentTimeMillis()),"3 days","chadur","nqma notes");
        String key = eventsDatasource.push().getKey();
        eventsDatasource.child(key).setValue(event);
    }
}