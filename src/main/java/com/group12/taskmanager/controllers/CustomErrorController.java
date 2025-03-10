package com.group12.taskmanager.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute("javax.servlet.error.status_code");

        String errorMessage = "Ocurrió algo totalmente inesperado.";
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            switch (statusCode) {
                case 404:
                    errorMessage = "Error 404 - Página no encontrada.";
                    break;
                case 500:
                    errorMessage = "Error 500 - Error interno del servidor.";
                    break;
                case 403:
                    errorMessage = "Error 403 - Acceso prohibido.";
                    break;
                default:
                    errorMessage = "Código de error: " + statusCode;
            }
        }

        model.addAttribute("errorMessage", errorMessage);
        return "error"; // Uses the error.mustache
    }
}
