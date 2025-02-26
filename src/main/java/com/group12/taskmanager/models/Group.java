package com.group12.taskmanager.models;

import com.group12.taskmanager.services.GroupUserService;
import com.group12.taskmanager.services.ProjectService;

import java.util.List;

public class Group {
    private static int globalID = 0;
    private int id;
    private String name;
    private List<User> users;
    private List<Project> projects;
    private final GroupUserService GROUP_USER_SERVICE = GroupUserService.getInstance();
    private final ProjectService PROJECT_SERVICE = ProjectService.getInstance();

    public Group(String name, User firstUser) {
        Group.globalID++;
        this.id = globalID;
        this.name = name;
        this.users = GROUP_USER_SERVICE.getGroupUsers(this.id);
        this.projects = PROJECT_SERVICE.getProjectsByGroup(this);
        users.add(firstUser);
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

    public List<User> getUsers() {
        return users;
    }
    public void addUser(User user) {
        users.add(user);
        GROUP_USER_SERVICE.addEntry(this, user); // Actualizar tabla relación
        user.updateGroups(this); // Actualizar registro de grupos del usuario
    }

    public List<Project> getProjects() {
        projects = PROJECT_SERVICE.getProjectsByGroup(this);
        return projects;
    }
    public void addProject(String projectName) {
        projects = PROJECT_SERVICE.getProjectsByGroup(this);
        Project newProject = new Project(projectName, this);
        projects.add(newProject);
        PROJECT_SERVICE.addProject(newProject);
    }
}
