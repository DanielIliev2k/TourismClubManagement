package com.example.tourismclubmanagement.comparators;

import com.example.tourismclubmanagement.models.Group;
import com.example.tourismclubmanagement.models.User;
import com.example.tourismclubmanagement.models.UserGroup;

import java.util.Comparator;
import java.util.List;

public class GroupComparator implements Comparator<Group> {
    private User user;

    public GroupComparator(User user) {
        this.user = user;
    }

    @Override
    public int compare(Group group1, Group group2) {
        boolean favorited1 = false;
        boolean favorited2 = false;
        if (isGroupFavorited(group1.getGroupInfo().getId())){
            favorited1 = true;
        }
        if (isGroupFavorited(group2.getGroupInfo().getId())){
            favorited2 = true;
        }

        if (favorited1 && !favorited2) {
            return -1;
        } else if (!favorited1 && favorited2) {
            return 1;
        }

        String date1 = group1.getGroupInfo().getDateCreated().toString();
        String date2 = group2.getGroupInfo().getDateCreated().toString();

        return date1.compareToIgnoreCase(date2);
    }

    private boolean isGroupFavorited(String groupId) {
        List<UserGroup> favoritedGroups = user.getGroups();

        for (UserGroup userGroup : favoritedGroups) {
            if (userGroup.getGroupId().equals(groupId) && userGroup.getFavourite()) {
                return true;
            }
        }
        return false;
    }
}