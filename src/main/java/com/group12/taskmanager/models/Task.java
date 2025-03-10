package com.group12.taskmanager.models;

public class Task {
    private static int globalID = 0;
    private int id;
    private String title;
    private String description;
    private int projectID;
    private String imagePath;

    public Task(String title, String description, int projectID, String imagePath) {
        Task.globalID = Task.globalID + 1;
        this.id = Task.globalID;
        this.title = title;
        this.description = description;
        this.projectID = projectID;
        this.imagePath = imagePath;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
