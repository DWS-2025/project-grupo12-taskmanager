package com.group12.taskmanager.services;

import com.group12.taskmanager.models.Group;
import com.group12.taskmanager.models.Project;
import com.group12.taskmanager.models.User;
import com.group12.taskmanager.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    public List<Group> getAllGroups() {
        return groupRepository.findAllByOrderByIdDesc();
    }

    public void saveGroup(Group group) {
        groupRepository.save(group);
    }

    public Group findGroupById(int groupId) {
        return groupRepository.findByIdWithUsers(groupId);
    }
    public Group createGroup(String name, User owner) {
        Group newGroup = new Group(name, owner);
        newGroup.getUsers().add(owner);
        owner.getGroups().add(newGroup);
        saveGroup(newGroup);
        return groupRepository.save(newGroup);
    }

    public boolean updateGroupName(int groupId, String newName) {
        Optional<Group> optionalGroup = groupRepository.findById(groupId);
        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            group.setName(newName);
            groupRepository.save(group);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean deleteGroup(int groupId, User currentUser) {
        Group group = findGroupById(groupId);

        if (group != null) {
            if (group.getOwner().getId().equals(currentUser.getId()) || currentUser.getId().equals(1)) {
                for (Project project : projectService.getProjectsByGroup(group)) {
                    projectService.deleteProject(project.getId());
                }
                if (currentUser.getId().equals(1) && group.getName().startsWith("USER_")) {
                    userService.deleteUser(group.getOwner().getId(), currentUser);
                } else {
                    groupRepository.delete(group);
                }
                return true;
            }
        }

        return false;
    }

    @Transactional
    public void removeUserFromGroup(Group group, User user) {
        group.getUsers().remove(user); // sincroniza memoria
        groupRepository.deleteUserFromGroup(group.getId(), user.getId()); // elimina en BBDD
    }

}
