package com.proyecto.stockio.controller;

import com.proyecto.stockio.model.Role;
import com.proyecto.stockio.model.Usuario;
import com.proyecto.stockio.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        return "usuarios";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario) {
        // aquí el usuario viene sin rol desde registro
        // opcional: podrías dejarlo sin rol o poner por defecto USUARIO
        usuarioService.guardarUsuario(usuario);
        return "redirect:/login";
    }

    @PostMapping("/actualizarRol")
    public String actualizarRol(@RequestParam Long id,
                                @RequestParam Role rol) {

        Usuario usuario = usuarioService.obtenerPorId(id);
        if (usuario != null) {
            usuario.setRol(Set.of(rol));
            usuarioService.guardarUsuario(usuario);
        }

        return "redirect:/usuarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return "redirect:/usuarios";
    }
}
