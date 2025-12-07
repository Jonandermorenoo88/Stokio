package com.proyecto.stockio.controller;

import com.proyecto.stockio.model.Almacen;
import com.proyecto.stockio.repository.AlmacenRepository;
import com.proyecto.stockio.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/almacenes")
public class AlmacenController {

    @Autowired
    private AlmacenRepository almacenRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    public String listarAlmacenes(Model model) {
        model.addAttribute("almacenes", almacenRepository.findAll());
        return "almacenes/index";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("almacen", new Almacen());
        model.addAttribute("listaProductos", productoRepository.findAll()); // Para el desplegable
        return "almacenes/form";
    }

    @PostMapping("/guardar")
    public String guardarAlmacen(@ModelAttribute Almacen almacen) {
        almacenRepository.save(almacen);
        return "redirect:/almacenes";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Almacen almacen = almacenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de almacén inválido: " + id));
        model.addAttribute("almacen", almacen);
        model.addAttribute("listaProductos", productoRepository.findAll());
        return "almacenes/form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarAlmacen(@PathVariable Long id) {
        almacenRepository.deleteById(id);
        return "redirect:/almacenes";
    }
}
