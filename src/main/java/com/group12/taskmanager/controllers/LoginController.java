package com.group12.taskmanager.controllers;

import com.group12.taskmanager.models.User;
import com.group12.taskmanager.services.GroupService;
import com.group12.taskmanager.services.GroupUserService;
import com.group12.taskmanager.services.TaskService;
import com.group12.taskmanager.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    private final UserService USER_SERVICE;

    public LoginController() {
        USER_SERVICE = UserService.getInstance();
        USER_SERVICE.addUser(new User("admin", "admin@admin.com", "eoHYeHEXe76Jn"));
        USER_SERVICE.addUser(new User("test", "test@test.com", "eoHYeHEXe5g54"));
        USER_SERVICE.addUser(new User("Roi", "roi@roi.com", "eoHYeHEXe5g54"));
        USER_SERVICE.addUser(new User("Roberto", "rob@rob.com", "eoHYeHEXe5g54"));

        GroupUserService.getInstance().createGroup("PRUEBA", 1);
        GroupUserService.getInstance().addEntry(GroupService.getInstance().findGroupById(5), UserService.getInstance().findUserById(2));
    }

    @GetMapping("/")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/projects"; // Redirige a la lista de proyectos si ya está autenticado
        }
        return "login"; // Renderiza login.mustache
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        User user = USER_SERVICE.findUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("user", user);
            return "redirect:/projects"; // Redirige a proyectos
        }
        model.addAttribute("error", "Usuario o contraseña incorrectos");
        return "login"; // Muestra el login con el mensaje de error
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Cierra sesión
        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "redirect:/";
    }
    @PostMapping("/register")
    public String register(@RequestParam String new_username,
                           @RequestParam String email,
                           @RequestParam String new_password,
                           @RequestParam String confirm_password,
                           Model model) {
        if (USER_SERVICE.findUserByUsername(new_username) != null) {
            model.addAttribute("error", "El usuario ya existe");
            return "redirect:/";
        }
        if (USER_SERVICE.findUserByEmail(email) != null) {
            model.addAttribute("error", "El email ya está registrado");
            return "redirect:/";
        }
        if (!new_password.equals(confirm_password)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "redirect:/";
        }
        User newUser = new User(new_username, email, new_password);
        USER_SERVICE.addUser(newUser);
        model.addAttribute("success_message", "Registro exitoso, inicia sesión");
        return "redirect:/";
    }

}
