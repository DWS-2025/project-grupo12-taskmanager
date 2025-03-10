package com.group12.taskmanager.services;

import com.group12.taskmanager.models.Group;
import com.group12.taskmanager.models.Project;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {
    private static GroupService instance; // Única instancia
    private final List<Group> GROUPS;

    private GroupService() {
        this.GROUPS = new ArrayList<>();
    }
    public static GroupService getInstance() {
        if (instance == null) {
            synchronized (GroupService.class) {
                if (instance == null) {
                    instance = new GroupService();
                }
            }
        }
        return instance;
    }

    public List<Group> getAllGroups() {
        return GROUPS;
    }
    public void addGroup(Group group) {
        if (!GROUPS.contains(group))
            GROUPS.add(group);
    }

    public Group findGroupById(int id) {
        for (Group group : GROUPS) {
            if (group.getId() == id) {
                return group;
            }
        }
        return null;
    }

    public boolean removeGroup(int groupId, String userName) {
        Group group = findGroupById(groupId);
        if (group != null && !group.getName().equals("USER_"+userName)) {
            // Eliminar todos los proyectos asociados al grupo
            List<Project> projectsToRemove = group.getProjects();
            for (Project project : projectsToRemove) {
                ProjectService.getInstance().removeProject(project.getId());
            }
            return GROUPS.removeIf(g -> g.getId() == groupId);
        }
        return false;
    }
}
