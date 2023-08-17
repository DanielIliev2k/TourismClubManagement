package com.example.tourismclubmanagement.comparators;

import com.example.tourismclubmanagement.models.UserGroup;

import java.util.Comparator;

public class UserGroupComparator implements Comparator<UserGroup> {

    public UserGroupComparator() {
    }

    @Override
    public int compare(UserGroup group1, UserGroup group2) {

        if (group1.getFavourite() && !group2.getFavourite()) {
            return -1;
        } else if (!group1.getFavourite() && group2.getFavourite()) {
            return 1;
        }


        return 0;
    }
}