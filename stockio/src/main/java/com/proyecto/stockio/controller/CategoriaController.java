package com.proyecto.stockio.controller;

import com.proyecto.stockio.model.Categoria;
import com.proyecto.stockio.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping("/nueva")
    public String nuevaCategoria(@RequestParam(required = false) Long almacenId, Model model) {
        model.addAttribute("categoria", new Categoria());
        model.addAttribute("almacenId", almacenId);
        return "categorias/form";
    }

    @PostMapping("/guardar")
    public String guardarCategoria(@ModelAttribute Categoria categoria,
            @RequestParam(required = false) Long almacenId,
            Model model) {
        try {
            categoriaRepository.save(categoria);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            model.addAttribute("error", "Error: Ya existe una categoría con ese nombre.");
            model.addAttribute("almacenId", almacenId);
            return "categorias/form";
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar la categoría: " + e.getMessage());
            model.addAttribute("almacenId", almacenId);
            return "categorias/form";
        }

        if (almacenId != null) {
            return "redirect:/almacenes/" + almacenId;
        }
        return "redirect:/almacenes";
    }

    @GetMapping("/editar/{id}")
    public String editarCategoria(@PathVariable Long id, @RequestParam(required = false) Long almacenId, Model model) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no válida: " + id));
        model.addAttribute("categoria", categoria);
        model.addAttribute("almacenId", almacenId);
        return "categorias/form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id, @RequestParam(required = false) Long almacenId) {
        // TODO: Comprobar si hay productos en esta categoría antes de borrar para
        // evitar error de FK
        // O dejar que falle y capturar la excepción
        try {
            categoriaRepository.deleteById(id);
        } catch (Exception e) {
            // Podríamos redirigir con un error, pero por ahora redirigimos simple
            return "redirect:/almacenes/" + (almacenId != null ? almacenId : "")
                    + "?error=No se puede eliminar la categoría porque tiene productos asociados";
        }

        if (almacenId != null) {
            return "redirect:/almacenes/" + almacenId;
        }
        return "redirect:/almacenes";
    }
}
