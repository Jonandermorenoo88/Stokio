package com.proyecto.stockio.controller;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping("/")
    public String inicio() {
        return "index"; // templates/index.html
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // templates/login.html
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
