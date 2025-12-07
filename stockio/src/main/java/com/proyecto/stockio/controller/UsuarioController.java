package com.proyecto.stockio.controller;

import com.proyecto.stockio.model.Role;
import com.proyecto.stockio.model.Usuario;
import com.proyecto.stockio.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // LISTAR USUARIOS
    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        return "usuarios"; // usuarios.html
    }

    // GUARDAR USUARIO (desde registro)
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario) {
        // No asignamos rol por defecto. El usuario debe esperar a que el admin le
        // asigne uno.
        // if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
        // usuario.getRol().add(Role.USUARIO);
        // }
        usuarioService.guardarUsuario(usuario);
        return "redirect:/login";
    }

    // ACTUALIZAR ROL
    @PostMapping("/actualizarRol")
    public String actualizarRol(@RequestParam Long id,
            @RequestParam String rol) {

        Usuario usuario = usuarioService.obtenerPorId(id);
        if (usuario == null) {
            return "redirect:/usuarios";
        }

        // Determinar el rol nuevo
        Role nuevoRol = null;
        if ("ADMIN".equalsIgnoreCase(rol)) {
            nuevoRol = Role.ADMIN;
        } else if ("USUARIO".equalsIgnoreCase(rol)) {
            nuevoRol = Role.USUARIO;
        }

        if (nuevoRol == null) {
            return "redirect:/usuarios";
        }

        // 💥 IMPORTANTE: trabajar con el Set mutable que ya tiene el usuario
        usuario.getRol().clear(); // vaciamos los roles anteriores
        usuario.getRol().add(nuevoRol); // añadimos el nuevo rol

        usuarioService.guardarUsuario(usuario);

        return "redirect:/usuarios";
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return "redirect:/usuarios";
    }
}
