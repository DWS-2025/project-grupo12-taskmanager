package com.group12.taskmanager.models;

import com.group12.taskmanager.services.TaskService;

import java.util.ArrayList;
import java.util.List;

public class Project {
    private static int globalID = 0;
    private int id;
    private String name;
    private Group group; // Relationship with a group
    private List<Task> tasks;
    private final TaskService TASKSERVICE;

    public Project(String name, Group group) {
        Project.globalID += 1;
        id = Project.globalID;
        this.name = name;
        this.group = group;
        TASKSERVICE = TaskService.getInstance();
        this.tasks = TASKSERVICE.getProjectTasks(this);
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

    public Group getGroup() {
        return group;
    }
    public void setGroup(Group group) {
        this.group = group;
    }

    public List<Task> getTasks() {
        tasks = TASKSERVICE.getProjectTasks(this);
        return tasks;
    }
    public void addTask(String title, String description, String imgPath) {
        tasks = TASKSERVICE.getProjectTasks(this);
        Task newTask = new Task(title, description, this.getId(), imgPath);
        tasks.add(newTask);
        TASKSERVICE.addTask(newTask);
    }
    public void removeTask(int taskID) {
        List<Task> updatedTasks = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getId() != taskID) {
                updatedTasks.add(t); // 🔹 Only add tasks that are NOT the one being removed
            }
        }
        tasks = updatedTasks; // 🔹 Replace the task list with the new list
        TASKSERVICE.removeTask(taskID); // 🔹 Then, remove the task from the service
    }

    public void remove() {
        // Remove all tasks associated with this project
        for (Task task : new ArrayList<>(tasks)) { // Copy to avoid ConcurrentModificationException
            TASKSERVICE.removeTask(task.getId());
        }
        tasks.clear(); // Clear the project's tasks
    }

}
