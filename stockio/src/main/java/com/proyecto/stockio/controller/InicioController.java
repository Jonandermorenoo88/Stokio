package com.proyecto.stockio.controller;

import com.proyecto.stockio.model.Role;
import com.proyecto.stockio.model.Usuario;
import com.proyecto.stockio.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class InicioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String inicio() {
        return "index"; // templates/index.html
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // templates/login.html
    }

    /*
     * @PostMapping("/login")
     * public String loginPost(@RequestParam String email,
     * 
     * @RequestParam String password,
     * Model model) {
     * 
     * Optional<Usuario> usuarioOpt = usuarioService.obtenerPorEmail(email);
     * 
     * // 1) Usuario no existe
     * if (usuarioOpt.isEmpty()) {
     * model.addAttribute("error", "El correo no existe");
     * return "login";
     * }
     * 
     * Usuario usuario = usuarioOpt.get();
     * 
     * // 2) Contraseña incorrecta
     * if (!usuario.getPassword().equals(password)) {
     * model.addAttribute("error", "Contraseña incorrecta");
     * return "login";
     * }
     * 
     * // 3) Usuario sin rol asignado todavía
     * if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
     * model.addAttribute("info",
     * "Tu cuenta está pendiente de que un administrador te asigne permisos. Inténtalo más tarde."
     * );
     * return "login";
     * }
     * 
     * // 4) Si es ADMIN → panel
     * if (usuario.getRol().contains(Role.ADMIN)) {
     * return "redirect:/panel";
     * }
     * 
     * // 5) Si es USUARIO → almacén
     * if (usuario.getRol().contains(Role.USUARIO)) {
     * return "redirect:/almacen";
     * }
     * 
     * // 6) Cualquier otro caso raro
     * model.addAttribute("error",
     * "No tienes un rol válido asignado. Contacta con el administrador.");
     * return "login";
     * }
     */

    @GetMapping("/panel")
    public String panel() {
        return "panel"; // templates/panel.html
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro"; // templates/registro.html
    }
}
