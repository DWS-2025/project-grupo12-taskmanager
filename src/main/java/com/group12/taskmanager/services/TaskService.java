package com.group12.taskmanager.services;

import com.group12.taskmanager.models.Project;
import com.group12.taskmanager.models.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {
    private static TaskService instance; // Única instancia
    private List<Task> tasks; // Simulación de base de datos

    private TaskService() {
        tasks =  new ArrayList<>();
    }
    public static TaskService getInstance() {
        if (instance == null) {
            synchronized (TaskService.class) {
                if (instance == null) {
                    instance = new TaskService();
                }
            }
        }
        return instance;
    }

    public List<Task> getAllTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(int id) {
        Task task = findTaskById(id);
        tasks.remove(task);
        System.out.println("Removed task: " + task);
    }

    public void updateTask(int id, String title, String description, String imagePath) {
        Task task = findTaskById(id);
        if (title != null)
            task.setTitle(title);
        if (description != null)
            task.setDescription(description);
        if (imagePath != null)
            task.setImagePath(imagePath);
        System.out.println("Updated task: " + task + " => id: " + id);
    }

    public Task findTaskById(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }

    public List<Task> getProjectTasks(Project project) {
        List<Task> projectTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getProjectID() == project.getId()) {
                projectTasks.add(task);
            }
        }
        return projectTasks;
    }

}

