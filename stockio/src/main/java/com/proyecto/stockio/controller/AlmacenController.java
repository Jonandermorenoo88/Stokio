package com.proyecto.stockio.controller;

import com.proyecto.stockio.model.Almacen;
import com.proyecto.stockio.model.Inventario;
import com.proyecto.stockio.model.Producto;
import com.proyecto.stockio.repository.AlmacenRepository;
import com.proyecto.stockio.repository.InventarioRepository;
import com.proyecto.stockio.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/almacenes")
public class AlmacenController {

    @Autowired
    private AlmacenRepository almacenRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private com.proyecto.stockio.service.ProductoService productoService;

    @Autowired
    private com.proyecto.stockio.service.AlbaranService albaranService;

    // Vista principal: Lista de almacenes con totales
    @GetMapping
    public String listarAlmacenes(Model model) {
        List<Almacen> almacenes = almacenRepository.findAll();
        List<AlmacenDTO> almacenesDTO = almacenes.stream().map(almacen -> {
            List<Inventario> inventarios = inventarioRepository.findByAlmacen(almacen);

            Integer totalCantidad = inventarios.stream()
                    .mapToInt(Inventario::getCantidad)
                    .sum();

            Double totalValor = inventarios.stream()
                    .mapToDouble(iv -> iv.getCantidad()
                            * (iv.getProducto().getPrecio() != null ? iv.getProducto().getPrecio() : 0.0))
                    .sum();

            return new AlmacenDTO(almacen, totalCantidad, totalValor);
        }).collect(Collectors.toList());

        model.addAttribute("almacenes", almacenesDTO);
        return "almacenes/index";
    }

    // Vista detalle: Inventario de un almacén específico
    @GetMapping("/{id}")
    public String verAlmacen(@PathVariable Long id, Model model) {
        Almacen almacen = almacenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));

        List<Inventario> inventario = inventarioRepository.findByAlmacen(almacen);

        model.addAttribute("almacen", almacen);
        model.addAttribute("inventario", inventario);
        // Pasamos lista de productos filtrados por ubicación para el modal/formulario
        // de añadir stock
        model.addAttribute("todosLosProductos", productoService.encontrarDisponiblesParaAlmacen(almacen));
        return "almacenes/detalle";
    }

    // Crear nuevo almacén
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("almacen", new Almacen());
        return "almacenes/form";
    }

    @PostMapping("/guardar")
    public String guardarAlmacen(@ModelAttribute Almacen almacen) {
        almacenRepository.save(almacen);
        return "redirect:/almacenes";
    }

    // Editar almacén
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Almacen almacen = almacenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("almacen", almacen);
        return "almacenes/form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarAlmacen(@PathVariable Long id) {
        // Opcional: Borrar inventario asociado primero o tener CascadeType.REMOVE en la
        // entidad (no hecho aqui por simplicidad)
        almacenRepository.deleteById(id);
        return "redirect:/almacenes";
    }

    // Añadir stock (producto) a un almacén
    @PostMapping("/{almacenId}/agregarProducto")
    public String agregarProducto(@PathVariable Long almacenId,
            @RequestParam Long productoId,
            @RequestParam Integer cantidad) {

        Almacen almacen = almacenRepository.findById(almacenId).orElseThrow();
        Producto producto = productoService.obtenerPorId(productoId);
        if (producto == null)
            throw new IllegalArgumentException("Producto no encontrado");

        // Usar AlbaranService para registrar la entrada y el movimiento
        albaranService.registrarEntrada(almacen, producto, cantidad, null); // TODO: Pasar usuario autenticado

        return "redirect:/almacenes/" + almacenId;
    }

    // DTO simple para la vista
    public static class AlmacenDTO {
        private Almacen almacen;
        private Integer totalCantidad;
        private Double totalValor;

        public AlmacenDTO(Almacen almacen, Integer totalCantidad, Double totalValor) {
            this.almacen = almacen;
            this.totalCantidad = totalCantidad;
            this.totalValor = totalValor;
        }

        public Almacen getAlmacen() {
            return almacen;
        }

        public Integer getTotalCantidad() {
            return totalCantidad;
        }

        public Double getTotalValor() {
            return totalValor;
        }
    }
}
