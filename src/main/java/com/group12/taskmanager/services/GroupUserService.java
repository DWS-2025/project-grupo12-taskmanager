package com.group12.taskmanager.services;

import com.group12.taskmanager.models.Group;
import com.group12.taskmanager.models.Group_User;
import com.group12.taskmanager.models.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupUserService {
    private static GroupUserService instance;
    private final List<Group_User> GROUP_USERS = new ArrayList<>();
    private final GroupService GROUP_SERVICE = GroupService.getInstance();
    private final UserService USER_SERVICE = UserService.getInstance();

    private GroupUserService() {
    }
    public static GroupUserService getInstance() {
        if (instance == null) {
            synchronized (GroupUserService.class) {
                if (instance == null) {
                    instance = new GroupUserService();
                }
            }
        }
        return instance;
    }

    public void addEntry(Group group, User user) {
        GROUP_USERS.add(new Group_User(group.getId(), user.getId()));
        GROUP_SERVICE.addGroup(group); // validation of existence in .addGroup()
        USER_SERVICE.addUser(user); // validation of existence in .addUser()
    }

    public List<User> getGroupUsers(int groupID) {
        List<User> list = new ArrayList<>();
        for (Group_User entry : GROUP_USERS) {
            if (groupID == entry.getIdGroup()) {
                list.add(USER_SERVICE.findUserById(entry.getIdUser()));
            }
        }
        return list;
    }
    public boolean removeUserFromGroup(int userId, int groupId, User currentUser) {
        Group group = GROUP_SERVICE.findGroupById(groupId);
        if (group == null) {
            return false; // Group not found
        }
        // Check if the current user is the owner of the group
        if (!group.isOwner(currentUser.getId())) {
            return false; // Not authorized
        }
        if (currentUser.getId() == userId) {
            return false; // Cannot remove yourself, you must delete the project
        }

        GROUP_USERS.removeIf(entry -> entry.getIdGroup() == groupId && entry.getIdUser() == userId);

        return true;
    }

    public List<Group> getUserGroups(int userID) {
        List<Group> list = new ArrayList<>();
        for (Group_User entry : GROUP_USERS) {
            if (entry.getIdUser() == userID) {
                list.add(GROUP_SERVICE.findGroupById(entry.getIdGroup()));
            }
        }
        return list;
    }

    public void createGroup(String name, int userId) {
        Group newGroup = new Group(name, USER_SERVICE.findUserById(userId)); // Generate a unique ID
        GROUP_SERVICE.addGroup(newGroup);
        GROUP_USERS.add(new Group_User(newGroup.getId(), userId)); // Associate user with the group
    }

    public boolean leaveGroup(int userId, int groupId) {
        return GROUP_USERS.removeIf(entry -> entry.getIdUser() == userId && entry.getIdGroup() == groupId);
    }

    public boolean updateGroupName(int groupId, String newName) {
        Group group = GROUP_SERVICE.findGroupById(groupId);
        if (group != null) {
            group.setName(newName);
            return true;
        }
        return false;
    }

    public boolean deleteGroup(int groupId, User user) {
        Group group = GROUP_SERVICE.findGroupById(groupId);
        if (group != null && group.getOwnerID() == user.getId()) {
            GROUP_USERS.removeIf(entry -> entry.getIdGroup() == groupId);
            return GROUP_SERVICE.removeGroup(groupId, user.getName());
        }
        return false;
    }

    public boolean addUsersToGroup(int groupId, List<Integer> userIds, User currentUser) {
        Group group = GROUP_SERVICE.findGroupById(groupId);
        if (group == null) {
            return false; // The group does not exist
        }

        // Check if the user trying to add members is the owner of the group
        if (!group.isOwner(currentUser.getId())) {
            return false; // Not authorized
        }

        for (Integer userId : userIds) {
            User user = USER_SERVICE.findUserById(userId);
            if (user != null) {
                GROUP_USERS.add(new Group_User(group.getId(), user.getId())); // Add user-group relationship
            }
        }

        return true;
    }
}
