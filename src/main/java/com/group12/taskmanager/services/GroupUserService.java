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
    private final UserService USER_SERVICE =  UserService.getInstance();

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
        GROUP_SERVICE.addGroup(group); // validacion de existencia en .addGroup()
        USER_SERVICE.addUser(user); // validacion de existencia en .addUser()
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
            return false; // Grupo no encontrado
        }
        // Verificar si el usuario actual es el propietario del grupo
        if (!group.isOwner(currentUser.getId())) {
            return false; // No autorizado
        }
        if (currentUser.getId() == userId) {
            return false; // No se puede eliminar a sí mismo tiene que eliminar el proyecto
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
        Group newGroup = new Group(name, USER_SERVICE.findUserById(userId)); // Generar un ID único
        GROUP_SERVICE.addGroup(newGroup);
        GROUP_USERS.add(new Group_User(newGroup.getId(), userId)); // Asociar usuario con el grupo
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
            return false; // El grupo no existe
        }

        // Verificar si el usuario que intenta añadir es el propietario del grupo
        if (!group.isOwner(currentUser.getId())) {
            return false; // No autorizado
        }

        for (Integer userId : userIds) {
            User user = USER_SERVICE.findUserById(userId);
            if (user != null) {
                GROUP_USERS.add(new Group_User(group.getId(), user.getId())); // Agregar la relación usuario-grupo
            }
        }

        return true;
    }
}
