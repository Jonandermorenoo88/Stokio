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
    public String panel(org.springframework.security.core.Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isJefeAlmacen = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_JEFE_ALMACEN"));

        boolean isAuxiliar = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AUXILIAR_ALMACEN"));

        boolean isUsuario = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USUARIO"));

        if (isAdmin) {
            return "panel"; // Admin va al panel general
        } else if (isJefeAlmacen || isAuxiliar) {
            return "panel_jefe"; // Jefe y Auxiliar van al panel de gestión (sin usuarios)
        } else if (isUsuario) {
            return "redirect:/almacenes"; // Usuario va directo al almacén
        } else {
            // Usuario sin rol (pendiente de aprobación)
            return "pendiente";
        }
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro"; // templates/registro.html
    }
}
