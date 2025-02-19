package com.group12.taskmanager.controllers;

import com.group12.taskmanager.models.Task;
import com.group12.taskmanager.services.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    //1. Mostrar lista de tareas en index.html
    @GetMapping("/")
    public String getTasks(Model model) {
        List<Task> tasks = taskService.getAllTasks();
        model.addAttribute("tasks", tasks);
        return "project"; // Renderiza "project.mustache"
    }

    //2. Guardar una nueva tarea desde el formulario
    @PostMapping("/save_task")
    @ResponseBody
    public String saveTask(@RequestParam String title, @RequestParam String description) {
        Task newTask = new Task(title, description);
        taskService.addTask(newTask); // Guardar la tarea en TaskService
        return "Tarea guardada correctamente"; // Respuesta en texto plano
    }

    @GetMapping("/new_task")
    public String newTask() {
        return "new_task"; // Devuelve el archivo new_task.mustache
    }

}

