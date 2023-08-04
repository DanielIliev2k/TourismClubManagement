package com.example.tourismclubmanagement.comparators;

import com.example.tourismclubmanagement.models.Role;
import com.example.tourismclubmanagement.models.User;
import com.example.tourismclubmanagement.models.UserInGroupInfo;

import java.util.Comparator;
import java.util.List;

public class UserComparator implements Comparator<User> {
    private List<UserInGroupInfo> usersInGroup;
    public UserComparator(List<UserInGroupInfo> usersInGroup) {
        this.usersInGroup = usersInGroup;
    }

    @Override
    public int compare(User user1, User user2) {
        if (getUserRole(user1.getUserInfo().getId())>getUserRole(user2.getUserInfo().getId())){
            return -1;
        }
        else if (getUserRole(user1.getUserInfo().getId())==getUserRole(user2.getUserInfo().getId())){
            return 0;
        }
        if (getUserRole(user1.getUserInfo().getId())<getUserRole(user2.getUserInfo().getId())){
            return 1;
        }

        String name1 = user1.getUserInfo().getName();
        String name2 = user2.getUserInfo().getName();

        return name1.compareToIgnoreCase(name2);
    }

    private int getUserRole(String userId) {
        for (UserInGroupInfo userInGroup : usersInGroup) {
            if (userInGroup.getId().equals(userId)) {
                if (userInGroup.getRole().equals(Role.ADMIN)){
                    return 0;
                }
                else if (userInGroup.getRole().equals(Role.OWNER)){
                    return 1;
                }
                else if (userInGroup.getRole().equals(Role.USER)){
                    return -1;
                }
            }
        }
        return 2;
    }
}