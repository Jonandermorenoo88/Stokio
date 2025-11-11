package com.proyecto.stockio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping("/")
    public String inicio() {
        // Carga el index.html que está en src/main/resources/static/
        return "/index";
    }

    @GetMapping("/login")
    public String login() {
        // Cuando tengas login.html en static/
        return "/login";
    }
}
