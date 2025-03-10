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
            if (project.getGroup() != null && project.getGroup().getId() == group.getId()) {
                list.add(project);
            }
        }
        return list;
    }
    public void removeProject(int projectId) {
        Project project = findById(projectId);
        if (project != null) {
            project.remove();
            PROJECTS.remove(project);
        }
    }
    public void updateProject(Project updatedProject) {
        for (int i = 0; i < PROJECTS.size(); i++) {
            if (PROJECTS.get(i).getId() == updatedProject.getId()) {
                PROJECTS.set(i, updatedProject);
                System.out.println("✅ Proyecto actualizado en la lista: " + updatedProject.getName());
                return;
            }
        }
        System.out.println("❌ No se encontró el proyecto en la lista.");
    }
}
