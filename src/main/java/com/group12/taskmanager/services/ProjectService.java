package com.group12.taskmanager.services;

import com.group12.taskmanager.models.Group;
import com.group12.taskmanager.models.Project;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {
    private static ProjectService instance; // Única instancia
    private final List<Project> PROJECTS;

    private ProjectService() {
        this.PROJECTS = new ArrayList<>();  // Simulación de base de datos
    }
    public static ProjectService getInstance() {
        if (instance == null) {
            synchronized (ProjectService.class) {
                if (instance == null) {
                    instance = new ProjectService();
                }
            }
        }
        return instance;
    }

    public List<Project> getAllProjects() {
        return PROJECTS;
    }

    public void addProject(Project project) {
        PROJECTS.add(project);
    }

    public Project findById(int id) {
        if (! PROJECTS.isEmpty()) {
            for (Project project : PROJECTS) {
                if (project.getId() == id) {
                    return project;
                }
            }
        }
        return null;
    }

    public List<Project> getProjectsByGroup(Group group) {
        List<Project> list = new ArrayList<>();
        for (Project project : PROJECTS) {
            if (project.getGroup().getId() == group.getId()) {
                list.add(project);
            }
        }
        return list;
    }

}
