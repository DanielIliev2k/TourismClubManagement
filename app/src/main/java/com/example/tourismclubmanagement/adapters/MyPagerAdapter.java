package com.example.tourismclubmanagement.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.tourismclubmanagement.R;
import com.example.tourismclubmanagement.models.Event;
import com.example.tourismclubmanagement.models.User;
import com.example.tourismclubmanagement.models.UserInGroupInfo;

import java.util.ArrayList;
import java.util.List;


public class MyPagerAdapter extends PagerAdapter {
    private OnCompleteListener listener;
    public interface OnCompleteListener {
        void onComplete();
    }

    public void setOnCompleteListener(OnCompleteListener listener) {
        this.listener = listener;
    }
    private final LayoutInflater inflater;
    private final List<View> pages = new ArrayList<>();
    private final EventRecyclerViewAdapter eventRecyclerViewAdapter;
    private final UsersRecyclerViewAdapter usersRecyclerViewAdapter;
    private final ImageRecyclerViewAdapter imageRecyclerViewAdapter;
    public MyPagerAdapter(LayoutInflater inflater,List<Event> events,List<User> users,List<Uri> images, List<UserInGroupInfo> usersInGroupList) {
        this.inflater = inflater;
        eventRecyclerViewAdapter = new EventRecyclerViewAdapter(events);
        usersRecyclerViewAdapter = new UsersRecyclerViewAdapter(users, usersInGroupList);
        imageRecyclerViewAdapter = new ImageRecyclerViewAdapter(images);
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View page = null;
        switch (position) {
            case 0:
                page = inflater.inflate(R.layout.events_page, container, false);
                RecyclerView recyclerViewEvents = page.findViewById(R.id.events_list_container);
                recyclerViewEvents.setLayoutManager(new LinearLayoutManager(container.getContext()));
                recyclerViewEvents.setAdapter(eventRecyclerViewAdapter);
                break;
            case 1:
                page = inflater.inflate(R.layout.members_page, container, false);
                RecyclerView recyclerViewUsers = page.findViewById(R.id.members_list_container);
                recyclerViewUsers.setLayoutManager(new LinearLayoutManager(container.getContext()));
                recyclerViewUsers.setAdapter(usersRecyclerViewAdapter);
                break;
            case 2:
                page = inflater.inflate(R.layout.photos_page, container, false);
                RecyclerView recyclerViewImages = page.findViewById(R.id.imageRecyclerView);
                GridLayoutManager layoutManager = new GridLayoutManager(container.getContext(),2);
                recyclerViewImages.setLayoutManager(layoutManager);
                recyclerViewImages.setAdapter(imageRecyclerViewAdapter);
                break;
        }
        container.addView(page);
        pages.add(page);
        if (position==getCount()-1)
        {
            this.listener.onComplete();
        }
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
    public View getPage(Integer pageNum){
        return pages.get(pageNum);
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
    public void updateUsers(List<User> users){
        usersRecyclerViewAdapter.updateUsersList(users);
    }
    public void updateEvents(List<Event> events){
        eventRecyclerViewAdapter.updateEventsList(events);
    }
    public void updateImages(List<Uri> images){
        imageRecyclerViewAdapter.updateImagesList(images);
    }
    public void updateImageReferences(List<String> imageReferences){
        imageRecyclerViewAdapter.updateImageReferencesList(imageReferences);
    }
    public EventRecyclerViewAdapter getEventRecyclerViewAdapter() {
        return eventRecyclerViewAdapter;
    }

    public UsersRecyclerViewAdapter getUsersRecyclerViewAdapter() {
        return usersRecyclerViewAdapter;
    }
    public ImageRecyclerViewAdapter getImageRecyclerViewAdapter(){
        return imageRecyclerViewAdapter;
    }
    public void updateUsersInGroupList(List<UserInGroupInfo> usersInGroupList){
        usersRecyclerViewAdapter.updateUsersInGroupList(usersInGroupList);
    }
}
