package com.group12.taskmanager.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group12.taskmanager.services.GroupUserService;

import java.util.List;

public class User {
    private static int globalID = 0;
    private int id;
    private String name;
    private String email;
    private String password;
    @JsonIgnore
    private List<Group> groups;
    GroupUserService groupUserService = GroupUserService.getInstance();

    public User(String name, String email, String password) {
        User.globalID++;
        this.id = User.globalID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.groups = groupUserService.getUserGroups(this.id); // get the user's groups (should be an empty list for a new user)
        Group newGroup = new Group("USER_" + this.name, this);
        groups.add(newGroup);
        groupUserService.addEntry(newGroup, this); // adds new records to their respective tables + relationship table
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public List<Group> getGroups() {
        return groups;
    }
    public void updateGroups(Group group) {
        this.groups.add(group);
    }
}
