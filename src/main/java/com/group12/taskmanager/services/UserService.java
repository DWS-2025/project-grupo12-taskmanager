package com.group12.taskmanager.services;

import com.group12.taskmanager.models.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static UserService instance; // Única instancia
    private final List<User> USERS;

    private UserService() {
        USERS = new ArrayList<>();
    }

    public static UserService getInstance() {
        if (instance == null) {
            synchronized (UserService.class) {
                if (instance == null) {
                    instance = new UserService();
                }
            }
        }
        return instance;
    }

    public List<User> getAllUsers() {
        return USERS;
    }

    public void addUser(User user) {
        USERS.add(user);
    }

    public User findUserById(int id) {
        for (User user : USERS) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    public User findUserByUsername(String userName) {
        for (User user : USERS) {
            if (user.getName().equals(userName)) {
                return user;
            }
        }
        return null;
    }

    public User findUserByEmail(String email) {
        for (User user : USERS) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    public List<User> searchUsersByNameExcludingGroup(String query, int groupId) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        String lowerQuery = query.toLowerCase();
        List<User> groupUsers = GroupUserService.getInstance().getGroupUsers(groupId);

        return USERS.stream()
                .filter(user -> user.getName().toLowerCase().contains(lowerQuery))
                .filter(user -> !groupUsers.contains(user)) // Excluir usuarios que ya están en el grupo
                .distinct()
                .collect(Collectors.toList());
    }

    public boolean deleteUser(int userId, User currentUser) {
        User userToRemove = findUserById(userId); // Buscar el usuario

        if (userToRemove == null) {
            System.out.println("No se encontró el usuario con ID: " + userId);
            return false; // Usuario no encontrado
        }

        // Verificar que el usuario actual está intentando eliminar su propia cuenta
        if (currentUser.getId() != userId) {
            System.out.println("No autorizado para eliminar esta cuenta.");
            return false; // No puede eliminar otras cuentas
        }

        // Eliminar al usuario de la base de datos
        USERS.remove(userToRemove);
        System.out.println("Cuenta eliminada correctamente: " + userId);

        return true;
    }


}