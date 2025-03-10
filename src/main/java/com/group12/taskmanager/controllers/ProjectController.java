package com.group12.taskmanager.controllers;

import com.group12.taskmanager.models.Group;
import com.group12.taskmanager.models.Project;
import com.group12.taskmanager.models.Task;
import com.group12.taskmanager.models.User;
import com.group12.taskmanager.services.GroupService;
import com.group12.taskmanager.services.ProjectService;
import com.group12.taskmanager.services.TaskService;
import com.group12.taskmanager.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
public class ProjectController {
    private final ProjectService PROJECT_SERVICE;
    private final TaskService TASK_SERVICE;

    // Test Data
    public ProjectController() {
        this.PROJECT_SERVICE = ProjectService.getInstance();
        this.TASK_SERVICE = TaskService.getInstance();
        for (int i = 1; i <= 5; i++) {
            Project newProject = new Project("Proyecto_"+i, GroupService.getInstance().findGroupById(1));
            PROJECT_SERVICE.addProject(newProject);
            for (int j = 0; j < 10; j++) {
                TASK_SERVICE.addTask(new Task("tarea_"+j, "Esto es un ejemplo"+i+j, newProject.getId(), null));
            }
        }
    }

    @GetMapping("/projects")
    public String getProjects(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/"; // Si no está autenticado, redirigir al login
        }
        model.addAttribute("user", currentUser);

        List<Project> projects = new ArrayList<>();
        for (Group group : currentUser.getGroups()) {
            projects.addAll(PROJECT_SERVICE.getProjectsByGroup(group));
        }
        model.addAttribute("projects", projects);

        return "index";
    }

    @PostMapping("/save_project")
    public String saveProject(@RequestParam String name, @RequestParam int userId) {
        User user = UserService.getInstance().findUserById(userId);
        if (user == null) {
            return "redirect:/"; // manages the errors if users doesnt exists
        }

        Project newProject = new Project(name, user.getGroups().getFirst()); // This will be changed, this part asign every new project to the personal group of the current user
        PROJECT_SERVICE.addProject(newProject);
        return "redirect:/projects";
    }

    @GetMapping("/new_project")
    public ResponseEntity<?> newProject() {
        return ResponseEntity.ok(Collections.singletonMap("mensaje", "Abriendo modal"));
    }

    @GetMapping("/project/{id}")
    public String getProjectById(@PathVariable int id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/"; // if not authenticated, return login
        }
        Project project = PROJECT_SERVICE.findById(id);
        List<Task> tasks = TASK_SERVICE.getProjectTasks(project);
        if (project != null) {
            model.addAttribute("idproject", project.getId());
            model.addAttribute("tasks", tasks);
        } else {
            model.addAttribute("idproject", null);
            model.addAttribute("tasks", new ArrayList<>());
        }
        return "project";
    }

    @PostMapping("/project/{id}/save_task")
    @ResponseBody
    public String saveTask(
            @PathVariable int id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) MultipartFile image) {

        String imagePath = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/200px-No_image_available.svg.png"; // Imagen de prueba

        if (image != null && !image.isEmpty()) {
            try {

                String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";
                File uploadFolder = new File(uploadDir);
                if (!uploadFolder.exists()) {
                    uploadFolder.mkdirs(); // Crea la carpeta si no existe
                }

                // Generar un nombre de archivo único
                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                File destinationFile = new File(uploadDir + fileName);

                // Guardar el archivo en la carpeta de uploads
                image.transferTo(destinationFile);

                // Ruta accesible desde el navegador
                imagePath = "/uploads/" + fileName;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Project currentProject = PROJECT_SERVICE.findById(id);
        currentProject.addTask(title, description, imagePath);

        return "redirect:/project/" + id;
    }

    @DeleteMapping("/project/{id}/delete_task")
    public ResponseEntity<?> deleteTask(@PathVariable int id, @RequestParam int taskId) {
        Project currentProject = PROJECT_SERVICE.findById(id);
        if (currentProject == null) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", "Proyecto no encontrado"));
        }
        currentProject.removeTask(taskId);

        // 🔹 forces update in the tasks list
        List<Task> updatedTasks = TASK_SERVICE.getProjectTasks(currentProject);
        boolean removed = updatedTasks.stream().noneMatch(t -> t.getId() == taskId);

        if (removed) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Tarea eliminada correctamente"));
        } else {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", "Tarea no encontrada"));
        }
    }

    @PutMapping("/project/{id}/edit_task")
    public ResponseEntity<?> editTask(
            @PathVariable int id,
            @RequestParam int taskId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) String imagePath) {

        Task task = TASK_SERVICE.findTaskById(taskId);
        if (task == null) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", "Tarea no encontrada"));
        }

        String newImagePath = imagePath; // Mantener la imagen original si no se sube una nueva

        if (image != null && !image.isEmpty()) {
            try {
                // Ruta de almacenamiento
                String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";
                File uploadFolder = new File(uploadDir);
                if (!uploadFolder.exists()) {
                    uploadFolder.mkdirs();
                }

                // Generar un nombre de archivo único
                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                File destinationFile = new File(uploadDir + fileName);

                // Guardar el archivo
                image.transferTo(destinationFile);

                // Actualizar el imagePath
                newImagePath = "/uploads/" + fileName;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        TASK_SERVICE.updateTask(taskId, title, description, newImagePath);

        return ResponseEntity.ok(Collections.singletonMap("message", "Tarea actualizada correctamente"));
    }


    @DeleteMapping("/project/{id}/delete_project")
    public ResponseEntity<?> deleteProject(@PathVariable int id) {
        Project project = PROJECT_SERVICE.findById(id);
        if (project == null) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", "Proyecto no encontrado"));
        }

        PROJECT_SERVICE.removeProject(id);

        boolean removed = PROJECT_SERVICE.findById(id) == null;
        if (removed) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Proyecto eliminado correctamente"));
        } else {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "Error al eliminar el proyecto"));
        }
    }
    @PutMapping("/project/{id}/edit_project")
    public ResponseEntity<?> editProject(@PathVariable int id, @RequestParam String name) {
        Project project = PROJECT_SERVICE.findById(id);
        if (project == null) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", "Proyecto no encontrado"));
        }

        project.setName(name); // 🔹 Actualiza el nombre en la instancia del proyecto
        PROJECT_SERVICE.updateProject(project); // 🔹 Guarda el cambio en la base de datos

        return ResponseEntity.ok(Collections.singletonMap("success", true));
    }
}
