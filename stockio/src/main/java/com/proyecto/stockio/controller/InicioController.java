package com.proyecto.stockio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class InicioController {

    @GetMapping("/")
    public String inicio() {
        return "index"; // asegúrate de tener templates/index.html
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // templates/login.html (aunque esté vacío)
    }

    @PostMapping("/login")
    public String loginPost() {
        // simulación temporal de login
        return "redirect:/panel";
    }

    @GetMapping("/panel")
    public String panel() {
        return "panel"; // templates/panel.html
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro"; // templates/registro.html
    }

}
