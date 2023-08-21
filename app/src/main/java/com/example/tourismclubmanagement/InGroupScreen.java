package com.example.tourismclubmanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tourismclubmanagement.adapters.ChatRecyclerViewAdapter;
import com.example.tourismclubmanagement.adapters.EventRecyclerViewAdapter;
import com.example.tourismclubmanagement.adapters.ImageRecyclerViewAdapter;
import com.example.tourismclubmanagement.adapters.MyPagerAdapter;
import com.example.tourismclubmanagement.adapters.ParticipantRecyclerViewAdapter;
import com.example.tourismclubmanagement.adapters.UsersRecyclerViewAdapter;
import com.example.tourismclubmanagement.comparators.ChatMessageComparator;
import com.example.tourismclubmanagement.comparators.EventComparator;
import com.example.tourismclubmanagement.comparators.ImageComparator;
import com.example.tourismclubmanagement.comparators.UserComparator;
import com.example.tourismclubmanagement.listeners.GroupListener;
import com.example.tourismclubmanagement.listeners.UserListener;
import com.example.tourismclubmanagement.models.ChatMessage;
import com.example.tourismclubmanagement.models.Event;
import com.example.tourismclubmanagement.models.EventInfo;
import com.example.tourismclubmanagement.models.EventParticipant;
import com.example.tourismclubmanagement.models.Group;
import com.example.tourismclubmanagement.models.Image;
import com.example.tourismclubmanagement.models.Role;
import com.example.tourismclubmanagement.models.Status;
import com.example.tourismclubmanagement.models.User;
import com.example.tourismclubmanagement.models.UserInGroupInfo;
import com.example.tourismclubmanagement.models.UserInfo;
import com.example.tourismclubmanagement.repositories.GroupRepository;
import com.example.tourismclubmanagement.repositories.UserRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class InGroupScreen extends AppCompatActivity{

    private AppCompatEditText eventNameField;
    private AppCompatEditText eventLocationField;
    private AppCompatEditText eventDurationField;
    private AppCompatEditText eventEquipmentField;
    private AppCompatEditText eventNotesField;
    private CheckBox eventParticipation;
    private AppCompatButton chatButton;
    private TimePicker eventTimePicker;
    private DatePicker eventDatePicker;
    private User user;
    private Dialog eventFormPopup;
    private Dialog addUserPopup;
    private Dialog deleteEventPopup;
    private Dialog deleteUserPopup;
    private Dialog userDetailsPopup;
    private Dialog eventDetailsPopup;
    private Dialog userRoleChangePopup;
    private Dialog chatPopup;
    private Dialog imagePopup;
    private Dialog participantsPopup;
    private Dialog deleteImagePopup;
    private MyPagerAdapter pagerAdapter;
    private Group currentGroup;
    private List<Event> eventsList;
    private List<User> currentGroupUsersList;
    private List<UserInGroupInfo> usersInGroupList;
    private GroupRepository groupRepository;
    private UserRepository userRepository;
    private boolean deleteEventMode;
    private boolean editEventMode;
    private boolean deleteUserMode;
    private ViewPager viewPager;
    private StorageReference storageReference;
    private List<Image> imagesToShow;
    private boolean firstUsersLoad;
    private FirebaseUser userAuth;
    private Role currentUserRole;
    private boolean deleteImageMode;
    private boolean firstGroupPull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_group_screen);
        instantiate();

    }
    public void getLoggedInUserInfo(String userId){
        userRepository.getUser(userId, new UserListener() {
            @Override
            public void onUserLoaded(User user) {
                InGroupScreen.this.user = user;
                currentUserRole = getCurrentUserRole();
            }
            @Override
            public void onFailure(DatabaseError error) {
                Toast.makeText(InGroupScreen.this,"Database Error!",Toast.LENGTH_LONG).show();
            }
        });
    }

    private Role getCurrentUserRole() {
       Role role = null;
        for (UserInGroupInfo userInGroup:currentGroup.getUsersInGroup()) {
            if (userInGroup.getId().equals(user.getUserInfo().getId())){
                role = userInGroup.getRole();
                break;
            }
        }
        return role;
    }

    public void getGroupFromDb(String groupId){
        groupRepository.getGroup(groupId, new GroupListener() {
            @Override
            public void onGroupLoaded(Group group) {
                if (group!= null){
                    currentGroup = group;
                    getLoggedInUserInfo(userAuth.getUid());
                    getUsersFromDb();
                    eventsList.clear();
                    eventsList = group.getEvents();
                    eventsList.sort(new EventComparator());
                    chatButton = findViewById(R.id.chatButton);
                    checkForNewChat();
                    if(!firstGroupPull){
                        pagerAdapter.updateEvents(eventsList);
                    }
                    else {
                        TextView groupNameField = findViewById(R.id.groupNameField);
                        groupNameField.setText(group.getGroupInfo().getGroupName());
                        getImagesFromStorage();
                        firstGroupPull=false;
                    }
                }
            }
            @Override
            public void onFailure(DatabaseError error) {
                Toast.makeText(InGroupScreen.this,"Database Error!",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getUsersFromDb(){

        userRepository.getAllUsers(new UserListener() {
            @Override
            public void onAllUsersLoaded(List<User> users) {
                currentGroupUsersList.clear();
                usersInGroupList.clear();
                for (UserInGroupInfo userInGroup:currentGroup.getUsersInGroup()) {
                    for (User user:users) {
                        if (user.getUserInfo().getId().equals(userInGroup.getId())){
                            currentGroupUsersList.add(user);
                            usersInGroupList.add(userInGroup);
                        }
                    }
                }
                if (!firstUsersLoad){
                    pagerAdapter.updateUsers(currentGroupUsersList);
                    pagerAdapter.updateUsersInGroupList(usersInGroupList);
                    currentGroupUsersList.sort(new UserComparator(usersInGroupList));
                }
                else{
                    firstUsersLoad=false;
                    currentGroupUsersList.sort(new UserComparator(usersInGroupList));
                    generatePages();
                }
            }
            @Override
            public void onFailure(DatabaseError error) {
                Toast.makeText(InGroupScreen.this,"Database Error!",Toast.LENGTH_LONG).show();
            }
        });
    }
    public void generatePages(){
        LayoutInflater pages_inflater = LayoutInflater.from(InGroupScreen.this);
        pagerAdapter = new MyPagerAdapter(pages_inflater,eventsList, currentGroupUsersList,imagesToShow,usersInGroupList);
        viewPager = findViewById(R.id.view_pager);
        pagerAdapter.setOnCompleteListener(new MyPagerAdapter.OnCompleteListener() {
            @Override
            public void onComplete() {
                if (currentUserRole.equals(Role.ADMIN)){
                    generateAdminButtons();
                }
                else if (currentUserRole.equals(Role.OWNER)){
                    generateOwnerButtons();
                }
                generateButtons();
            }
        });
        viewPager.setOffscreenPageLimit(2);
        eventsSetOnItemClickListener();
        usersSetOnItemClickListener();
        imagesSetOnItemClickListener();
        viewPager.setAdapter(pagerAdapter);


    }
    public void generateAdminButtons(){

        AppCompatButton addEventButton = pagerAdapter.getPage(0).findViewById(R.id.addEventButton);
        AppCompatButton editEventButton = pagerAdapter.getPage(0).findViewById(R.id.editEventButton);
        AppCompatButton addUserButton = pagerAdapter.getPage(1).findViewById(R.id.addUserButton);
        AppCompatButton deleteEventButton = pagerAdapter.getPage(0).findViewById(R.id.deleteEventButton);
        AppCompatButton uploadImageButton = pagerAdapter.getPage(2).findViewById(R.id.uploadImageButton);
        LinearLayoutCompat eventsPageButtonsContainer = pagerAdapter.getPage(0).findViewById(R.id.eventsPageButtonsContainer);
        LinearLayoutCompat membersPageButtonsContainer = pagerAdapter.getPage(1).findViewById(R.id.membersPageButtonsContainer);
        LinearLayoutCompat photosPageButtonsContainer = pagerAdapter.getPage(2).findViewById(R.id.photosPageButtonsContainer);
        eventsPageButtonsContainer.setVisibility(View.VISIBLE);
        membersPageButtonsContainer.setVisibility(View.VISIBLE);
        photosPageButtonsContainer.setVisibility(View.VISIBLE);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                editEventButton.setBackgroundResource(R.drawable.button_background);
                deleteEventButton.setBackgroundResource(R.drawable.button_background);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePictureToUpload();
            }
        });
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEventMode = false;
                editEventMode = false;
                editEventButton.setBackgroundResource(R.drawable.button_background);
                deleteEventButton.setBackgroundResource(R.drawable.button_background);
                showEventFormPopup(new Event());
            }
        });
        editEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editEventMode){
                    editEventButton.setBackgroundResource(R.drawable.edit_button_background);
                }
                else{
                    editEventButton.setBackgroundResource(R.drawable.button_background);
                }
                deleteEventButton.setBackgroundResource(R.drawable.button_background);
                editEventMode = !editEventMode;
                deleteEventMode = false;
            }
        });
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserMode = false;
                showAddUserEmailPopup(v);
            }
        });
        deleteEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!deleteEventMode){
                    deleteEventButton.setBackgroundResource(R.drawable.delete_button_background);
                }
                else{
                    deleteEventButton.setBackgroundResource(R.drawable.button_background);
                }
                editEventButton.setBackgroundResource(R.drawable.button_background);
                deleteEventMode=!deleteEventMode;
                editEventMode = false;
            }
        });


    }
    public void generateOwnerButtons(){
        AppCompatButton addEventButton = pagerAdapter.getPage(0).findViewById(R.id.addEventButton);
        AppCompatButton editEventButton = pagerAdapter.getPage(0).findViewById(R.id.editEventButton);
        AppCompatButton addUserButton = pagerAdapter.getPage(1).findViewById(R.id.addUserButton);
        AppCompatButton deleteEventButton = pagerAdapter.getPage(0).findViewById(R.id.deleteEventButton);
        AppCompatButton deleteUserButton = pagerAdapter.getPage(1).findViewById(R.id.deleteUserButton);
        AppCompatButton uploadImageButton = pagerAdapter.getPage(2).findViewById(R.id.uploadImageButton);
        AppCompatButton deleteImageButton = pagerAdapter.getPage(2).findViewById(R.id.deleteImageButton);
        LinearLayoutCompat eventsPageButtonsContainer = pagerAdapter.getPage(0).findViewById(R.id.eventsPageButtonsContainer);
        LinearLayoutCompat membersPageButtonsContainer = pagerAdapter.getPage(1).findViewById(R.id.membersPageButtonsContainer);
        LinearLayoutCompat photosPageButtonsContainer = pagerAdapter.getPage(2).findViewById(R.id.photosPageButtonsContainer);

        eventsPageButtonsContainer.setVisibility(View.VISIBLE);
        membersPageButtonsContainer.setVisibility(View.VISIBLE);
        photosPageButtonsContainer.setVisibility(View.VISIBLE);
        deleteImageButton.setVisibility(View.VISIBLE);
        deleteUserButton.setVisibility(View.VISIBLE);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                editEventButton.setBackgroundResource(R.drawable.button_background);
                deleteEventButton.setBackgroundResource(R.drawable.button_background);
                deleteUserButton.setBackgroundResource(R.drawable.button_background);
                deleteImageButton.setBackgroundResource(R.drawable.button_background);
                editEventMode = false;
                deleteEventMode = false;
                deleteUserMode = false;
                deleteImageMode = false;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!deleteImageMode){
                    deleteImageButton.setBackgroundResource(R.drawable.delete_button_background);
                }
                else{
                    deleteImageButton.setBackgroundResource(R.drawable.button_background);
                }
                deleteImageMode=!deleteImageMode;
            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImageButton.setBackgroundResource(R.drawable.button_background);
                choosePictureToUpload();
            }
        });
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEventMode = false;
                editEventMode = false;
                editEventButton.setBackgroundResource(R.drawable.button_background);
                deleteEventButton.setBackgroundResource(R.drawable.button_background);
                showEventFormPopup(new Event());
            }
        });
        editEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editEventMode){
                    editEventButton.setBackgroundResource(R.drawable.edit_button_background);
                }
                else{
                    editEventButton.setBackgroundResource(R.drawable.button_background);
                }
                deleteEventButton.setBackgroundResource(R.drawable.button_background);
                editEventMode = !editEventMode;
                deleteEventMode = false;
            }
        });
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserMode = false;
                deleteUserButton.setBackgroundResource(R.drawable.button_background);
                showAddUserEmailPopup(v);
            }
        });
        deleteEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!deleteEventMode){
                    deleteEventButton.setBackgroundResource(R.drawable.delete_button_background);
                }
                else{
                    deleteEventButton.setBackgroundResource(R.drawable.button_background);
                }
                editEventButton.setBackgroundResource(R.drawable.button_background);
                deleteEventMode=!deleteEventMode;
                editEventMode = false;
            }
        });
        deleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!deleteUserMode){
                    deleteUserButton.setBackgroundResource(R.drawable.delete_button_background);
                }
                else{
                    deleteUserButton.setBackgroundResource(R.drawable.button_background);
                }
                deleteUserMode=!deleteUserMode;
            }
        });

    }
    public void generateButtons(){
        AppCompatButton settingsButton = findViewById(R.id.settingsButton);
        AppCompatButton chatButton = findViewById(R.id.chatButton);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChatPopup();
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsPopupMenu(v);
            }
        });
    }
    private void showSettingsPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.settings_menu, popupMenu.getMenu());

        if (currentUserRole.equals(Role.USER)||currentUserRole.equals(Role.ADMIN)){
            popupMenu.getMenu().removeItem(R.id.settingsGroupButton);
        }
        if (currentUserRole.equals(Role.OWNER)){
            popupMenu.getMenu().removeItem(R.id.settingsLeaveGroup);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.settingsUserButton:
                        Intent userEditInfoIntent = new Intent(InGroupScreen.this,UserEditInfoScreen.class);
                        startActivity(userEditInfoIntent);
                        return true;
                    case R.id.settingsGroupButton:
                        Intent groupEditInfoIntent = new Intent(InGroupScreen.this,GroupEditInfoScreen.class);
                        groupEditInfoIntent.putExtra("groupId",currentGroup.getGroupInfo().getId());
                        startActivity(groupEditInfoIntent);
                        return true;
                    case R.id.settingsLeaveGroup:
                        showLeaveGroupPopup();
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    private void showLeaveGroupPopup() {
        Dialog confirmLeaveGroupPopup = new Dialog(InGroupScreen.this);
        confirmLeaveGroupPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmLeaveGroupPopup.setCancelable(true);
        confirmLeaveGroupPopup.setContentView(R.layout.confirm_leave_group_popup);
        confirmLeaveGroupPopup.show();
        TextView leaveGroupConfirmationText = confirmLeaveGroupPopup.findViewById(R.id.leaveGroupConfirmationText);
        AppCompatButton leaveGroupConfirmationButton = confirmLeaveGroupPopup.findViewById(R.id.leaveGroupConfirmationButton);
        leaveGroupConfirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveGroup();
            }
        });
        leaveGroupConfirmationText.setText("Are you sure you want to leave this group: " + currentGroup.getGroupInfo().getGroupName() + "?");
    }

    private void leaveGroup() {
        groupRepository.removeUserFromGroup(currentGroup.getGroupInfo().getId(),userAuth.getUid());
        Intent homeIntent = new Intent(InGroupScreen.this,UserHomeScreen.class);
        startActivity(homeIntent);
    }

    private void showChatPopup() {
        chatPopup = new Dialog(InGroupScreen.this);
        chatPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        chatPopup.setCancelable(true);
        chatPopup.setContentView(R.layout.chat_popup);
        groupRepository.updateUserLastLogin(currentGroup.getGroupInfo().getId(),user.getUserInfo().getId());
        int width = (int) (getResources().getDisplayMetrics().widthPixels);
        int height = (int) (getResources().getDisplayMetrics().heightPixels);
        Objects.requireNonNull(chatPopup.getWindow()).setLayout(width,height);
        RecyclerView chatRecyclerView = chatPopup.findViewById(R.id.chatRecyclerView);
        ChatRecyclerViewAdapter chatRecyclerViewAdapter = new ChatRecyclerViewAdapter(new ArrayList<>());
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(InGroupScreen.this));
        chatRecyclerView.setAdapter(chatRecyclerViewAdapter);
        groupRepository.getChat(currentGroup.getGroupInfo().getId(), new GroupListener() {
            @Override
            public void onChatLoaded(List<ChatMessage> messages) {
                messages.sort(new ChatMessageComparator());
                chatRecyclerViewAdapter.updateMessages(messages);
                chatRecyclerView.scrollToPosition(messages.size()-1);
            }
        });
        chatButton.setBackgroundResource(R.drawable.chat);
        AppCompatButton cancelPopupButton = chatPopup.findViewById(R.id.cancelPopupButton);
        cancelPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatPopup.cancel();
                groupRepository.updateUserLastLogin(currentGroup.getGroupInfo().getId(),user.getUserInfo().getId());
            }
        });
        AppCompatButton sendMessageButton = chatPopup.findViewById(R.id.sendMessageButton);
        AppCompatEditText chatMessageField = chatPopup.findViewById(R.id.chatMessageField);

        chatPopup.show();
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Objects.requireNonNull(chatMessageField.getText()).toString().equals("")){
                    groupRepository.addChatMessage(currentGroup.getGroupInfo().getId(),user.getUserInfo().getName(),chatMessageField.getText().toString());
                    chatMessageField.setText("");
                    }
            }
        });
        chatPopup.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                chatButton.setBackgroundResource(R.drawable.chat);
            }
        });
    }
    public void checkForNewChat(){
        if (currentGroup.getGroupInfo()!=null){
            groupRepository.getChat(currentGroup.getGroupInfo().getId(), new GroupListener() {
                @Override
                public void onChatLoaded(List<ChatMessage> messages) {
                    if(!messages.isEmpty()){
                        for (UserInGroupInfo userInGroup:currentGroup.getUsersInGroup()) {
                            if (userInGroup.getId().equals(user.getUserInfo().getId())){
                                if (userInGroup.getLastLogin()==null || messages.get(messages.size()-1).getDate().after(userInGroup.getLastLogin())){
                                    chatButton.setBackgroundResource(R.drawable.chat_notification);
                                }
                                break;
                            }
                        }
                    }
                }
            });
        }
    }
    public void choosePictureToUpload(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pagerAdapter.updateImages(imagesToShow);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode== RESULT_OK && data!=null) {
            List<Uri> imagesToUpload = new ArrayList<>();
            ClipData mClipData = data.getClipData();
            if (mClipData != null) {
                for (int i = 0; i < mClipData.getItemCount(); i++) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(mClipData.getItemAt(i).getUri());
                        int fileSize = inputStream.available();
                        inputStream.close();
                        if (fileSize > 10000000) {
                            Toast.makeText(InGroupScreen.this, "Some images are too big. Limit 10 mb!", Toast.LENGTH_LONG).show();
                        } else {
                            imagesToUpload.add(mClipData.getItemAt(i).getUri());
                            Toast.makeText(InGroupScreen.this,"Uploading Images ...",Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    int fileSize = inputStream.available();
                    inputStream.close();
                    if (fileSize > 10000000) {
                        Toast.makeText(InGroupScreen.this, "Some images are too big. Limit 10 mb!", Toast.LENGTH_LONG).show();
                    } else {
                        imagesToUpload.add(data.getData());
                        Toast.makeText(InGroupScreen.this,"Uploading Images ...",Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            uploadPictures(imagesToUpload);

        }
    }
    public void getImagesFromStorage(){
        imagesToShow.clear();
        StorageReference imagesReference = storageReference.child(currentGroup.getGroupInfo().getId());

        imagesReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                if (listResult.getItems().isEmpty()){
                    pagerAdapter.updateImages(imagesToShow);
                }
                else{
                    for(StorageReference file:listResult.getItems()){
                        Image image = new Image();
                        image.setReference(file.getName());
                        file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                image.setUri(uri);
                                imagesToShow.add(image);
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imagesToShow.sort(new ImageComparator());
                                pagerAdapter.updateImages(imagesToShow);
                            }
                        });

                    }
                }
            }
        });

    }
    private void uploadPictures(List<Uri> images) {
        for (Uri uri:images) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
            String id = sdf.format(new Date());
            StorageReference currentImageReference = storageReference.child(currentGroup.getGroupInfo().getId()+ "/" + id);
            currentImageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imagesToShow.add(new Image(uri,id));
                    pagerAdapter.updateImages(imagesToShow);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(InGroupScreen.this,"Image could not be uploaded",Toast.LENGTH_LONG).show();
                }
            });
        }

    }
    public void showEventFormPopup(Event event){
        eventFormPopup = new Dialog(InGroupScreen.this);
        eventFormPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        eventFormPopup.setCancelable(true);
        eventFormPopup.setContentView(R.layout.event_form_popup);
        int width = (int) (getResources().getDisplayMetrics().widthPixels);
        int height = (int) (getResources().getDisplayMetrics().heightPixels);
        eventFormPopup.getWindow().setLayout(width,height);
        AppCompatButton cancelPopupButton = eventFormPopup.findViewById(R.id.cancelPopupButton);
        cancelPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventFormPopup.cancel();
            }
        });

        eventFormPopup.show();
        instantiateEventFormElements();
        if (editEventMode){
            populateEventFields(event.getEventInfo());
        }
        AppCompatButton saveEventButton = eventFormPopup.findViewById(R.id.saveEventButton);
        TextView newEventNameError = eventFormPopup.findViewById(R.id.newEventNameError);
        AppCompatEditText eventNameField = eventFormPopup.findViewById(R.id.eventNameField);
        saveEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newEventNameError.setVisibility(View.INVISIBLE);
                if (!Objects.requireNonNull(eventNameField.getText()).toString().equals("")){
                    addFormEventToDb(event.getEventInfo());
                    eventFormPopup.cancel();
                }
                else {
                    newEventNameError.setText("Please input event name");
                    newEventNameError.setVisibility(View.VISIBLE);
                }

            }
        });
    }
    public void populateEventFields(EventInfo eventInfo){
        eventNameField.setText(eventInfo.getEventName());
        eventLocationField.setText(eventInfo.getLocation());
        eventDurationField.setText(eventInfo.getDuration());
        eventEquipmentField.setText(eventInfo.getEquipment());
        eventNotesField.setText(eventInfo.getNotes());
        Date date = eventInfo.getDepartureTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        eventDatePicker.init(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH),null);
        eventTimePicker.setHour(cal.get(Calendar.HOUR));
        eventTimePicker.setMinute(cal.get(Calendar.MINUTE));
        eventParticipation.setVisibility(View.INVISIBLE);
    }
    public void instantiateEventFormElements(){
        eventNameField = eventFormPopup.findViewById(R.id.eventNameField);
        eventLocationField = eventFormPopup.findViewById(R.id.eventLocationField);
        eventDurationField = eventFormPopup.findViewById(R.id.eventDurationField);
        eventEquipmentField = eventFormPopup.findViewById(R.id.eventEquipmentField);
        eventNotesField = eventFormPopup.findViewById(R.id.eventNotesField);
        eventDatePicker = eventFormPopup.findViewById(R.id.eventDatePicker);
        eventTimePicker = eventFormPopup.findViewById(R.id.eventTimePicker);
        eventParticipation = eventFormPopup.findViewById(R.id.participationCheck);
    }
    public void addFormEventToDb(EventInfo eventInfo){
        if (eventInfo==null){
            eventInfo=new EventInfo();
        }
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Bulgaria"));
        cal.set(Calendar.YEAR, eventDatePicker.getYear());
        cal.set(Calendar.MONTH, eventDatePicker.getMonth());
        cal.set(Calendar.DAY_OF_MONTH, eventDatePicker.getDayOfMonth());
        cal.set(Calendar.HOUR,eventTimePicker.getHour()-3);
        cal.set(Calendar.MINUTE,eventTimePicker.getMinute());
        Date eventTime = cal.getTime();
        eventInfo.setEventName(eventNameField.getText().toString());
        eventInfo.setDuration(eventDurationField.getText().toString());
        eventInfo.setLocation(eventLocationField.getText().toString());
        eventInfo.setEquipment(eventEquipmentField.getText().toString());
        eventInfo.setNotes(eventNotesField.getText().toString());
        eventInfo.setEventName(eventNameField.getText().toString());
        eventInfo.setDepartureTime(eventTime);
        if (eventInfo.getParticipation()==null){
            eventInfo.setParticipation(eventParticipation.isChecked());
        }
        if (eventInfo.getId()==null){
            groupRepository.addEvent(currentGroup.getGroupInfo().getId(),eventInfo);
        }
        else {
            groupRepository.updateEvent(currentGroup.getGroupInfo().getId(),eventInfo);
        }


    }
    public void instantiate(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userAuth = mAuth.getCurrentUser();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        eventsList = new ArrayList<>();
        currentGroupUsersList = new ArrayList<>();
        usersInGroupList = new ArrayList<>();
        imagesToShow = new ArrayList<>();
        deleteEventMode = false;
        editEventMode = false;
        deleteUserMode = false;
        deleteImageMode = false;
        firstUsersLoad = true;
        firstGroupPull = true;
        groupRepository = new GroupRepository();
        userRepository = new UserRepository();
        getGroupFromDb(this.getIntent().getStringExtra("groupId"));
    }
    public void showDeleteEventPopup(String eventId){
        deleteEventPopup = new Dialog(InGroupScreen.this);
        deleteEventPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deleteEventPopup.setCancelable(true);
        deleteEventPopup.setContentView(R.layout.delete_event_popup);
        int width = (int) (getResources().getDisplayMetrics().widthPixels);
        deleteEventPopup.getWindow().setLayout(width,deleteEventPopup.getWindow().getAttributes().height);
        AppCompatButton cancelPopupButton = deleteEventPopup.findViewById(R.id.cancelPopupButton);
        cancelPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEventPopup.cancel();
            }
        });
        TextView deletionEventName = deleteEventPopup.findViewById(R.id.deletionEventName);
        for (Event tempEvent:eventsList) {
            if (tempEvent.getEventInfo().getId().equals(eventId))
            {
                deletionEventName.setText(tempEvent.getEventInfo().getEventName());
                break;
            }
        }
        AppCompatButton confirmDeleteEventButton = deleteEventPopup.findViewById(R.id.confirmDeleteEventButton);
        confirmDeleteEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupRepository.deleteEvent(currentGroup.getGroupInfo().getId(),eventId);
                deleteEventPopup.cancel();
            }
        });
        deleteEventPopup.show();
    }
    public void showDeleteUserPopup(String userId){
        deleteUserPopup = new Dialog(InGroupScreen.this);
        deleteUserPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deleteUserPopup.setCancelable(true);
        deleteUserPopup.setContentView(R.layout.delete_user_popup);
        int width = (int) (getResources().getDisplayMetrics().widthPixels);
        deleteUserPopup.getWindow().setLayout(width,deleteUserPopup.getWindow().getAttributes().height);
        AppCompatButton cancelPopupButton = deleteUserPopup.findViewById(R.id.cancelPopupButton);
        cancelPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserPopup.cancel();
            }
        });
        TextView deletionUserUsername = deleteUserPopup.findViewById(R.id.deletionUserUsername);
        for (User user :currentGroupUsersList) {
            if (user.getUserInfo().getId().equals(userId)){
                deletionUserUsername.setText(user.getUserInfo().getName());
                break;
            }
        }
        AppCompatButton confirmDeleteUserButton = deleteUserPopup.findViewById(R.id.confirmDeleteUserButton);
        confirmDeleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupRepository.removeUserFromGroup(currentGroup.getGroupInfo().getId(),userId);
                deleteUserPopup.cancel();
            }
        });
        deleteUserPopup.show();
    }

    public void showAddUserEmailPopup(View view){
        addUserPopup = new Dialog(InGroupScreen.this);
        addUserPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addUserPopup.setCancelable(true);
        addUserPopup.setContentView(R.layout.add_user_popup);
        int width = (int) (getResources().getDisplayMetrics().widthPixels);
        addUserPopup.getWindow().setLayout(width,addUserPopup.getWindow().getAttributes().height);
        EditText newUserEmailField = addUserPopup.findViewById(R.id.newUserEmailField);
        AppCompatButton submitNewUserButton = addUserPopup.findViewById(R.id.submitNewUserButton);
        AppCompatButton cancelPopupButton = addUserPopup.findViewById(R.id.cancelPopupButton);
        cancelPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUserPopup.cancel();
            }
        });
        addUserPopup.show();
        submitNewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRepository.getUserIdByEmail(newUserEmailField.getText().toString(), new UserListener() {
                    @Override
                    public void onUserIdLoadedByEmail(String userId) {
                        groupRepository.addUserToGroup(currentGroup.getGroupInfo().getId(),userId,Role.USER);
                        addUserPopup.cancel();
                    }

                    @Override
                    public void onFailure(DatabaseError error) {
                        Toast.makeText(InGroupScreen.this,"Database Error!",Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

    private void eventsSetOnItemClickListener() {
        EventRecyclerViewAdapter eventsRecyclerViewAdapter = pagerAdapter.getEventRecyclerViewAdapter();
        eventsRecyclerViewAdapter.setOnItemClickListener(new EventRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String eventId) {
                if (deleteEventMode){
                    showDeleteEventPopup(eventId);
                }
                else if (editEventMode){
                    Event event = new Event();
                    for (Event tempEvent:eventsList) {
                        if (tempEvent.getEventInfo().getId().equals(eventId)){
                            event = tempEvent;
                            break;
                        }
                    }
                    showEventFormPopup(event);
                }
                else {
                    showEventDetailsPopup(eventId);
                }
            }
        });
    }
    private void usersSetOnItemClickListener() {
        UsersRecyclerViewAdapter usersRecycleViewAdapter = pagerAdapter.getUsersRecyclerViewAdapter();
        usersRecycleViewAdapter.setOnItemClickListener(new UsersRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String userId) {
                if (deleteUserMode){
                    showDeleteUserPopup(userId);
                }
                else{
                    showUserDetailsPopup(userId);
                }

            }
        });
    }
    private void imagesSetOnItemClickListener() {
        ImageRecyclerViewAdapter imagesRecycleViewAdapter = pagerAdapter.getImageRecyclerViewAdapter();
        imagesRecycleViewAdapter.setOnItemClickListener(new ImageRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Image image) {
                if(deleteImageMode){
                    showDeleteImagePopup(image);
                }
                else {
                    showImagePopup(image);
                }
            }
        });
    }

    public void showImagePopup(Image image) {
        imagePopup = new Dialog(InGroupScreen.this);
        imagePopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        imagePopup.setCancelable(true);
        imagePopup.setContentView(R.layout.image_popup);
        int width = (int) (getResources().getDisplayMetrics().widthPixels);
        int height = imagePopup.getWindow().getAttributes().height;
        imagePopup.getWindow().setLayout(width,height);
        AppCompatButton cancelPopupButton = imagePopup.findViewById(R.id.cancelPopupButton);
        cancelPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePopup.cancel();
            }
        });
        ImageView imageContainer = imagePopup.findViewById(R.id.imageContainer);
        Glide.with(imageContainer.getContext()).load(image.getUri()).into(imageContainer);
        imagePopup.show();
    }
    public void showDeleteImagePopup(Image image){
        deleteImagePopup = new Dialog(InGroupScreen.this);
        deleteImagePopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deleteImagePopup.setCancelable(true);
        deleteImagePopup.setContentView(R.layout.delete_image_popup);
        int width = (int) (getResources().getDisplayMetrics().widthPixels);
        int height = deleteImagePopup.getWindow().getAttributes().height;
        deleteImagePopup.getWindow().setLayout(width,height);
        AppCompatButton cancelPopupButton = deleteImagePopup.findViewById(R.id.cancelPopupButton);
        cancelPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImagePopup.cancel();
            }
        });
        ImageView imageContainer = deleteImagePopup.findViewById(R.id.imageContainer);
        Glide.with(imageContainer.getContext()).load(image.getUri()).into(imageContainer);
        deleteImagePopup.show();
        AppCompatButton confirmDeleteImageButton = deleteImagePopup.findViewById(R.id.confirmDeleteImageButton);
        confirmDeleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImage(image.getReference());
                deleteImagePopup.cancel();
            }
        });
    }
    private void deleteImage(String imageReference) {
        StorageReference currentGroupReference = storageReference.child(currentGroup.getGroupInfo().getId());
        currentGroupReference.child(imageReference).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                getImagesFromStorage();
            }
        });
    }

    public void showUserDetailsPopup(String userId){
        User displayedUser = new User();
        for (User tempUser: currentGroupUsersList) {
            if (tempUser.getUserInfo().getId().equals(userId)){
                displayedUser = tempUser;
                break;
            }
        }
        if (displayedUser.getUserInfo().getId()!=null){
            userDetailsPopup = new Dialog(InGroupScreen.this);
            userDetailsPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
            userDetailsPopup.setCancelable(true);
            userDetailsPopup.setContentView(R.layout.user_details_popup);
            int width = (int) (getResources().getDisplayMetrics().widthPixels);
            int height = userDetailsPopup.getWindow().getAttributes().height;
            userDetailsPopup.getWindow().setLayout(width,height);
            AppCompatButton cancelPopupButton = userDetailsPopup.findViewById(R.id.cancelPopupButton);
            cancelPopupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userDetailsPopup.cancel();
                }
            });
            TextView userDetailsNameField = userDetailsPopup.findViewById(R.id.userDetailsNameField);
            TextView userDetailsAgeField = userDetailsPopup.findViewById(R.id.userDetailsAgeField);
            TextView userDetailsHometownField = userDetailsPopup.findViewById(R.id.userDetailsHometownField);
            TextView userDetailsAdminField = userDetailsPopup.findViewById(R.id.userDetailsRoleField);
            AppCompatButton userDetailsChangeRole = userDetailsPopup.findViewById(R.id.userDetailsChangeRole);
            if (displayedUser.getUserInfo().getFirstLogin()&currentUserRole==Role.ADMIN){
                userDetailsNameField.setText(displayedUser.getUserInfo().getEmail());
            }
            else {
                userDetailsNameField.setText(displayedUser.getUserInfo().getName());
                userDetailsAgeField.setText(displayedUser.getUserInfo().getAge().toString());
                userDetailsHometownField.setText(displayedUser.getUserInfo().getHometown());
            }
            switch (currentUserRole){
                case ADMIN:
                    for (UserInGroupInfo userInGroupInfo:currentGroup.getUsersInGroup()) {
                        if (userInGroupInfo.getId().equals(displayedUser.getUserInfo().getId())){
                            userDetailsAdminField.setText(userInGroupInfo.getRole().toString());
                            userDetailsAdminField.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                    break;
                case OWNER:
                    for (UserInGroupInfo userInGroupInfo:currentGroup.getUsersInGroup()) {
                        if (userInGroupInfo.getId().equals(displayedUser.getUserInfo().getId())){
                            userDetailsAdminField.setText(userInGroupInfo.getRole().toString());
                            userDetailsAdminField.setVisibility(View.VISIBLE);
                            if (!userInGroupInfo.getRole().equals(Role.OWNER)){
                                userDetailsChangeRole.setVisibility(View.VISIBLE);
                                User finalDisplayedUser = displayedUser;
                                userDetailsChangeRole.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showConfirmRoleChangePopup(finalDisplayedUser.getUserInfo());
                                    }
                                });
                            }
                            break;
                        }
                    }
                    break;
            }

            userDetailsPopup.show();
        }

    }
    public void showConfirmRoleChangePopup(UserInfo userInfo){
        userRoleChangePopup = new Dialog(InGroupScreen.this);
        userRoleChangePopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        userRoleChangePopup.setCancelable(true);
        userRoleChangePopup.setContentView(R.layout.confirm_change_role_popup);
        int width = (int) (getResources().getDisplayMetrics().widthPixels);
        int height = userRoleChangePopup.getWindow().getAttributes().height;
        userRoleChangePopup.getWindow().setLayout(width,height);
        AppCompatButton changeUserRoleConfirmButton = userRoleChangePopup.findViewById(R.id.changeUserRoleConfirmButton);
        AppCompatButton cancelPopupButton = userRoleChangePopup.findViewById(R.id.cancelPopupButton);
        TextView changeUserRoleConfirmField = userRoleChangePopup.findViewById(R.id.changeUserRoleConfirmField);
        cancelPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRoleChangePopup.cancel();
            }
        });
        changeUserRoleConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupRepository.changeUserRole(currentGroup.getGroupInfo().getId(),userInfo.getId());
                TextView userDetailsRoleField = userDetailsPopup.findViewById(R.id.userDetailsRoleField);
                groupRepository.getUserInGroupOnce(currentGroup.getGroupInfo().getId(), userInfo.getId(), new GroupListener() {
                    @Override
                    public void onUserInGroupLoaded(UserInGroupInfo userInGroup) {
                        if (userInGroup.getRole().equals(Role.USER)){
                            userDetailsRoleField.setText(Role.ADMIN.toString());
                        }
                        else{
                            userDetailsRoleField.setText(Role.USER.toString());
                        }
                    }
                });
                userRoleChangePopup.cancel();
            }
        });
        changeUserRoleConfirmField.setText("Are you sure you want to change "+ userInfo.getName()+"'s role?");
        userRoleChangePopup.show();
    }
    public void showEventDetailsPopup(String eventId){
        Event event = new Event();
        for (Event tempEvent:eventsList) {
            if (tempEvent.getEventInfo().getId().equals(eventId)){
                event = tempEvent;
                break;
            }
        }
        if (event.getEventInfo().getId()!=null){
            eventDetailsPopup = new Dialog(InGroupScreen.this);
            eventDetailsPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
            eventDetailsPopup.setCancelable(true);
            eventDetailsPopup.setContentView(R.layout.event_details_popup);
            AppCompatButton cancelPopupButton = eventDetailsPopup.findViewById(R.id.cancelPopupButton);
            cancelPopupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventDetailsPopup.cancel();
                }
            });
            int width = (int) (getResources().getDisplayMetrics().widthPixels);
            int height = eventDetailsPopup.getWindow().getAttributes().height;
            eventDetailsPopup.getWindow().setLayout(width,height);
            TextView eventDetailsNameField = eventDetailsPopup.findViewById(R.id.eventDetailsNameField);
            TextView eventDetailsLocationField = eventDetailsPopup.findViewById(R.id.eventDetailsLocationField);
            TextView eventDetailsEquipmentField = eventDetailsPopup.findViewById(R.id.eventDetailsEquipmentField);
            TextView eventDetailsDurationField = eventDetailsPopup.findViewById(R.id.eventDetailsDurationField);
            TextView eventDetailsDepartureTimeField = eventDetailsPopup.findViewById(R.id.eventDetailsDepartureTimeField);
            TextView eventDetailsNotesField = eventDetailsPopup.findViewById(R.id.eventDetailsNotesField);
            TextView eventDetailsLocationText = eventDetailsPopup.findViewById(R.id.eventDetailsLocationText);
            TextView eventDetailsEquipmentText = eventDetailsPopup.findViewById(R.id.eventDetailsEquipmentText);
            TextView eventDetailsDurationText = eventDetailsPopup.findViewById(R.id.eventDetailsDurationText);
            TextView eventDetailsDepartureTimeText = eventDetailsPopup.findViewById(R.id.eventDetailsDepartureTimeText);
            TextView eventDetailsNotesText = eventDetailsPopup.findViewById(R.id.eventDetailsNotesText);
            TextView eventParticipationField = eventDetailsPopup.findViewById(R.id.participationStatusField);
            AppCompatButton eventParticipationButton = eventDetailsPopup.findViewById(R.id.participationButton);
            AppCompatButton showParticipantsButton = eventDetailsPopup.findViewById(R.id.showParticipantsButton);
            if (currentUserRole.equals(Role.USER)){
                showParticipantsButton.setVisibility(View.GONE);
            }
            if (event.getEventInfo().getLocation().equals("")){
                eventDetailsLocationField.setVisibility(View.GONE);
                eventDetailsLocationText.setVisibility(View.GONE);
            }
            else {
                eventDetailsLocationField.setText(event.getEventInfo().getLocation());
            }

            if (event.getEventInfo().getParticipation()){
                eventParticipationField.setVisibility(View.VISIBLE);
                eventParticipationButton.setVisibility(View.VISIBLE);
                groupRepository.getEventParticipant(currentGroup.getGroupInfo().getId(), eventId, user.getUserInfo().getId(), new GroupListener() {
                    @Override
                    public void onEventParticipantLoaded(EventParticipant eventParticipant) {
                        if(eventParticipant!=null) {
                            if (eventParticipant.getUserId().equals(user.getUserInfo().getId())){
                                switch (eventParticipant.getStatus()){
                                    case DENIED:
                                        eventParticipationField.setText("Participation Denied");
                                        eventParticipationButton.setVisibility(View.GONE);
                                        break;
                                    case PENDING:
                                        eventParticipationField.setText("Participation Pending");
                                        eventParticipationButton.setText("Withdraw");
                                        eventParticipationButton.setVisibility(View.VISIBLE);
                                        break;
                                    case ACCEPTED:
                                        eventParticipationField.setText("Participation Accepted");
                                        eventParticipationButton.setText("Withdraw");
                                        eventParticipationButton.setVisibility(View.VISIBLE);
                                        break;
                                    default:
                                        eventParticipationField.setVisibility(View.GONE);
                                        eventParticipationButton.setVisibility(View.GONE);
                                        break;
                                }
                            }
                            showParticipantsButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showParticipantsPopup(eventId);

                                }
                            });
                            eventParticipationButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    groupRepository.deleteParticipant(currentGroup.getGroupInfo().getId(),eventId,user.getUserInfo().getId());
                                    eventParticipationField.setText("Not Requested");
                                    eventParticipationButton.setText("Join");
                                }
                            });
                        }
                        else {
                            eventParticipationField.setText("Not Requested");
                            eventParticipationButton.setText("Join");
                            eventParticipationButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    EventParticipant newParticipant = new EventParticipant();
                                    newParticipant.setUserId(user.getUserInfo().getId());
                                    newParticipant.setUserName(user.getUserInfo().getName());
                                    if (currentUserRole.equals(Role.USER)){
                                        newParticipant.setStatus(Status.PENDING);
                                    }
                                    else {
                                        newParticipant.setStatus(Status.ACCEPTED);
                                    }
                                    groupRepository.addEventParticipant(currentGroup.getGroupInfo().getId(),eventId,newParticipant);
                                    if (newParticipant.getStatus().equals(Status.ACCEPTED)){
                                        eventParticipationField.setText("Participation Accepted");
                                        eventParticipationButton.setText("Withdraw");
                                    }
                                    if (newParticipant.getStatus().equals(Status.PENDING)){
                                        eventParticipationField.setText("Participation Pending");
                                        eventParticipationButton.setText("Withdraw");
                                    }
                                }
                            });

                        }
                    }
                });
            }
            else {
                showParticipantsButton.setVisibility(View.GONE);
                eventParticipationField.setVisibility(View.GONE);
                eventParticipationButton.setVisibility(View.GONE);
            }
            if (event.getEventInfo().getDepartureTime().toString().equals("")){
                eventDetailsDepartureTimeField.setVisibility(View.GONE);
                eventDetailsDepartureTimeText.setVisibility(View.GONE);
            }
            else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",Locale.ROOT);
                String date = sdf.format(event.getEventInfo().getDepartureTime());
                sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                String time =sdf.format(event.getEventInfo().getDepartureTime());
                eventDetailsDepartureTimeField.setText(date + "\n" +time);
            }
            if (event.getEventInfo().getEquipment().equals("")){
                eventDetailsEquipmentField.setVisibility(View.GONE);
                eventDetailsEquipmentText.setVisibility(View.GONE);
            }
            else {
                eventDetailsEquipmentField.setText(event.getEventInfo().getEquipment());
            }
            if (event.getEventInfo().getDuration().equals("")){
                eventDetailsDurationField.setVisibility(View.GONE);
                eventDetailsDurationText.setVisibility(View.GONE);
            }
            else {
                eventDetailsDurationField.setText(event.getEventInfo().getDuration());
            }
            if (event.getEventInfo().getNotes().equals("")){
                eventDetailsNotesField.setVisibility(View.GONE);
                eventDetailsNotesText.setVisibility(View.GONE);
            }
            else {
                eventDetailsNotesField.setText(event.getEventInfo().getNotes());
            }
            eventDetailsNameField.setText(event.getEventInfo().getEventName());
            eventDetailsPopup.show();

        }

    }

    private void showParticipantsPopup(String eventId) {
        participantsPopup = new Dialog(InGroupScreen.this);
        participantsPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        participantsPopup.setCancelable(true);
        participantsPopup.setContentView(R.layout.participants_popup);
        int width = (int) (getResources().getDisplayMetrics().widthPixels);
        int height = participantsPopup.getWindow().getAttributes().height;
        participantsPopup.getWindow().setLayout(width,height);
        AppCompatButton cancelPopupButton = participantsPopup.findViewById(R.id.cancelPopupButton);
        cancelPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                participantsPopup.cancel();
            }
        });
        RecyclerView participantsContainer = participantsPopup.findViewById(R.id.participantsContainer);
        TextView noParticipantsText = participantsPopup.findViewById(R.id.noParticipantsText);
        TextView pendingRequestsField = participantsPopup.findViewById(R.id.pendingRequestsField);
        TextView acceptedRequestsField = participantsPopup.findViewById(R.id.acceptedRequestsField);
        TextView allRequestsField = participantsPopup.findViewById(R.id.allRequestsField);
        TextView deniedRequestsField = participantsPopup.findViewById(R.id.deniedRequestsField);
        participantsContainer.setLayoutManager(new LinearLayoutManager(this));
        ParticipantRecyclerViewAdapter participantAdapter = new ParticipantRecyclerViewAdapter(new ArrayList<>(),eventId,currentGroup.getGroupInfo().getId(),groupRepository,userRepository);
        participantsContainer.setAdapter(participantAdapter);
        groupRepository.getAllEventParticipants(currentGroup.getGroupInfo().getId(), eventId, new GroupListener() {
            @Override
            public void onAllEventParticipantsLoaded(List<EventParticipant> eventParticipants) {
                int pendingRequests = 0;
                int acceptedRequests = 0;
                int deniedRequests = 0;
                if (!eventParticipants.isEmpty()){
                    noParticipantsText.setVisibility(View.GONE);
                    pendingRequestsField.setVisibility(View.VISIBLE);
                    acceptedRequestsField.setVisibility(View.VISIBLE);
                    allRequestsField.setVisibility(View.VISIBLE);
                    deniedRequestsField.setVisibility(View.VISIBLE);
                    for (EventParticipant participant:eventParticipants) {
                        if (participant.getStatus().equals(Status.PENDING)){
                            pendingRequests++;
                        }
                        if (participant.getStatus().equals(Status.ACCEPTED)){
                            acceptedRequests++;
                        }
                        if (participant.getStatus().equals(Status.DENIED)){
                            deniedRequests++;
                        }
                    }
                    pendingRequestsField.setText("Pending : " + pendingRequests);
                    acceptedRequestsField.setText("Accepted : " + acceptedRequests);
                    deniedRequestsField.setText("Denied : " + deniedRequests);
                    allRequestsField.setText("All : " + (pendingRequests + acceptedRequests + deniedRequests));
                    participantAdapter.updateParticipants(eventParticipants);
                }
            }
            @Override
            public void onFailure(DatabaseError error) {
                Toast.makeText(InGroupScreen.this,"Database Error!",Toast.LENGTH_LONG).show();
            }
        });

        participantsPopup.show();
    }
}