package com.proyecto.stockio.controller;

import com.proyecto.stockio.model.Almacen;
import com.proyecto.stockio.model.Producto;
import com.proyecto.stockio.repository.AlmacenRepository;
import com.proyecto.stockio.service.AlbaranService;
import com.proyecto.stockio.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/movimientos")
public class MovimientoController {

    @Autowired
    private AlmacenRepository almacenRepository;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private AlbaranService albaranService;

    @Autowired
    private com.proyecto.stockio.repository.CategoriaRepository categoriaRepository;

    // --- COMPRA (ENTRADA) ---
    @GetMapping("/compras/nueva")
    public String nuevaCompra(Model model) {
        model.addAttribute("almacenes", almacenRepository.findAll());
        model.addAttribute("productos", productoService.listarProductos()); // Mostrar todos para comprar
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "movimientos/compra";
    }

    @PostMapping("/compras/guardar")
    public String guardarCompra(@RequestParam Long almacenId,
            @RequestParam(required = false) Long productoId,
            @RequestParam Integer cantidad,
            @RequestParam(required = false) String nuevoNombre,
            @RequestParam(required = false) Double nuevoPrecio,
            @RequestParam(required = false) Long categoriaId,
            java.security.Principal principal) {

        if (almacenId == null)
            throw new IllegalArgumentException("Almacen ID required");
        Almacen almacen = almacenRepository.findById(almacenId).orElseThrow();
        Producto producto;

        if (nuevoNombre != null && !nuevoNombre.isEmpty()) {
            // Crear nuevo producto
            producto = new Producto();
            producto.setNombre(nuevoNombre);
            producto.setPrecio(nuevoPrecio);
            if (categoriaId != null) {
                producto.setCategoria(categoriaRepository.findById(categoriaId).orElse(null));
            }
            // La ubicación se podría inferir del almacén o dejar en blanco (Global)

            producto = productoService.guardarProducto(producto);
        } else {
            if (productoId == null) {
                throw new IllegalArgumentException("Debe seleccionar un producto o crear uno nuevo");
            }
            producto = productoService.obtenerPorId(productoId);
        }

        // Obtener usuario actual
        com.proyecto.stockio.model.Usuario usuario = null;
        if (principal != null) {
            usuario = com.proyecto.stockio.service.UsuarioService.obtenerUsuarioDesdePrincipal(principal,
                    usuarioService);
        }

        // Registrar Entrada (Compra)
        albaranService.registrarEntrada(almacen, producto, cantidad, usuario);

        return "redirect:/albaranes"; // Volver al historial
    }

    // --- VENTA (SALIDA) ---
    @GetMapping("/ventas/nueva")
    public String nuevaVenta(Model model) {
        model.addAttribute("almacenes", almacenRepository.findAll());
        model.addAttribute("productos", productoService.listarProductos());
        return "movimientos/venta";
    }

    @PostMapping("/ventas/guardar")
    public String guardarVenta(@RequestParam Long almacenId,
            @RequestParam Long productoId,
            @RequestParam Integer cantidad,
            java.security.Principal principal) {

        if (almacenId == null)
            throw new IllegalArgumentException("Almacen ID required");
        Almacen almacen = almacenRepository.findById(almacenId).orElseThrow();
        Producto producto = productoService.obtenerPorId(productoId);

        // Obtener usuario actual
        com.proyecto.stockio.model.Usuario usuario = null;
        if (principal != null) {
            usuario = com.proyecto.stockio.service.UsuarioService.obtenerUsuarioDesdePrincipal(principal,
                    usuarioService);
        }

        try {
            // Registrar Salida (Venta)
            albaranService.registrarSalida(almacen, producto, cantidad, usuario);
        } catch (IllegalArgumentException e) {
            // Si falta stock, podríamos redirigir con error
            return "redirect:/movimientos/ventas/nueva?error=" + e.getMessage();
        }

        return "redirect:/albaranes";
    }

    @Autowired
    private com.proyecto.stockio.service.UsuarioService usuarioService;
}
