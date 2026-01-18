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
    public String guardarUsuario(@ModelAttribute Usuario usuario, Model model) {
        // No asignamos rol por defecto. El usuario debe esperar a que el admin le
        // asigne uno.
        try {
            usuarioService.guardarUsuario(usuario);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            model.addAttribute("error", "Error: El correo electrónico ya está registrado.");
            return "registro";
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar el usuario: " + e.getMessage());
            return "registro";
        }
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
        } else if ("JEFE_ALMACEN".equalsIgnoreCase(rol)) {
            nuevoRol = Role.JEFE_ALMACEN;
        } else if ("AUXILIAR_ALMACEN".equalsIgnoreCase(rol)) {
            nuevoRol = Role.AUXILIAR_ALMACEN;
        }

        if (nuevoRol == null) {
            return "redirect:/usuarios";
        }

        // 💥 IMPORTANTE: trabajar con el Set mutable que ya tiene el usuario
        usuario.getRol().clear(); // vaciamos los roles anteriores
        usuario.getRol().add(nuevoRol); // añadimos el nuevo rol

        usuarioService.guardarUsuarioSinEncriptar(usuario);

        return "redirect:/usuarios";
    }

    // Defensive Handler: Redirigir GET accidental a /actualizarRol (ej: recargar
    // página) a la lista
    @GetMapping("/actualizarRol")
    public String actualizarRolGet() {
        return "redirect:/usuarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return "redirect:/usuarios";
    }
}
