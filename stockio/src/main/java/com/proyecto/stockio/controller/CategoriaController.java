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
}
