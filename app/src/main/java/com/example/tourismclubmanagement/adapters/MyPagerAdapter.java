package com.example.tourismclubmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.tourismclubmanagement.R;
import com.example.tourismclubmanagement.models.Event;

import java.util.ArrayList;
import java.util.List;


public class MyPagerAdapter extends PagerAdapter {
    private final LayoutInflater inflater;
    private List<Event> events = new ArrayList<>();
    public MyPagerAdapter(LayoutInflater inflater,List<Event> events) {
        this.inflater = inflater;
        this.events = events;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View page = null;
        switch (position) {
            case 0:

                page = inflater.inflate(R.layout.events_page, container, false);
                RecyclerView recyclerView = page.findViewById(R.id.events_list_container);
                recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
                recyclerView.setAdapter(new EventRecyclerViewAdapter(events));
                break;
            case 1:
                page = inflater.inflate(R.layout.members_page, container, false);
                break;
            case 2:
                page = inflater.inflate(R.layout.photos_page, container, false);
                break;
        }
        container.addView(page);
        return page;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
    @Override
    public void startUpdate(@NonNull ViewGroup container){

    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
    public void setEvents(List<Event> events){
        this.events = events;
    }
}
