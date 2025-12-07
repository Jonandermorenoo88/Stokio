package com.proyecto.stockio.controller;

import com.proyecto.stockio.model.Almacen;
import com.proyecto.stockio.model.Categoria;
import com.proyecto.stockio.model.Inventario;
import com.proyecto.stockio.model.Producto;
import com.proyecto.stockio.repository.AlmacenRepository;
import com.proyecto.stockio.repository.InventarioRepository;

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
    private com.proyecto.stockio.service.ProductoService productoService;

    @Autowired
    private com.proyecto.stockio.service.AlbaranService albaranService;

    @Autowired
    private com.proyecto.stockio.repository.CategoriaRepository categoriaRepository;

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
    // Vista detalle: Resumen de Categorías
    @GetMapping("/{id}")
    public String verAlmacen(@PathVariable Long id, Model model) {
        Almacen almacen = almacenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));

        List<Inventario> inventario = inventarioRepository.findByAlmacen(almacen);

        // Obtener TODAS las categorías del sistema
        List<Categoria> todasLasCategorias = categoriaRepository.findAll();

        // Calcular totales para cada categoría en ESTE almacén
        List<CategoriaDTO> categoriasDTO = todasLasCategorias.stream().map(cat -> {
            // Filtrar items de este almacén que pertenecen a esta categoría
            List<Inventario> itemsDeCategoria = inventario.stream()
                    .filter(i -> i.getProducto().getCategoria() != null
                            && i.getProducto().getCategoria().getId() != null
                            && i.getProducto().getCategoria().getId().equals(cat.getId()))
                    .collect(Collectors.toList());

            int cantidadTotal = itemsDeCategoria.stream().mapToInt(Inventario::getCantidad).sum();
            double valorTotal = itemsDeCategoria.stream().mapToDouble(i -> i.getCantidad()
                    * (i.getProducto().getPrecio() != null ? i.getProducto().getPrecio() : 0)).sum();

            return new CategoriaDTO(cat.getNombre(), cantidadTotal, valorTotal);
        }).collect(Collectors.toList());

        // Manejar items "Sin Categoría" si es necesario (opcional)
        // Por ahora nos centramos en mostrar las categorías explícitas

        model.addAttribute("almacen", almacen);
        model.addAttribute("categorias", categoriasDTO);
        // esVacio ya no se basa solo en inventario, sino en si hay categorias para
        // mostrar (siempre habrá si se crean)
        // pero podemos mantenerlo false para que siempre muestre la tabla si hay
        // categorías
        model.addAttribute("esVacio", categoriasDTO.isEmpty());
        // Añadir lista de productos para el formulario de añadir stock
        model.addAttribute("productosDisponibles", productoService.listarProductos());

        return "almacenes/detalle";
    }

    // Vista detalle de una categoría específica (Lista de productos)
    @GetMapping("/{id}/categoria")
    public String verCategoria(@PathVariable Long id, @RequestParam("nombre") String nombreCategoria, Model model) {
        Almacen almacen = almacenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));

        List<Inventario> inventarioCompleto = inventarioRepository.findByAlmacen(almacen);

        // Filtrar por categoría seleccionada
        List<Inventario> productosCategoria = inventarioCompleto.stream()
                .filter(item -> {
                    Categoria catObj = item.getProducto().getCategoria();
                    String cat = (catObj != null) ? catObj.getNombre() : null;
                    if ("Sin Categoría".equals(nombreCategoria)) {
                        return cat == null || cat.isBlank();
                    }
                    return nombreCategoria.equals(cat);
                })
                .collect(Collectors.toList());

        // Obtener productos que pertenecen a questa categoría para el desplegable de
        // añadir stock
        // (Podríamos optimizar esto con una query en repo, por ahora filtramos en
        // memoria los de todo el sistema o solo los de esa categoría)
        // La idea es "Añadir Stock" -> Se añade inventario de un producto existente.
        // Si el usuario quiere añadir un producto de esta categoria al almacen, debe
        // elegir un Producto que tenge esta categoria.
        List<Producto> productosDeLaCategoria = productoService.listarProductos().stream()
                .filter(p -> {
                    Categoria c = p.getCategoria();
                    return c != null && c.getNombre().equals(nombreCategoria);
                })
                .collect(Collectors.toList());

        model.addAttribute("almacen", almacen);
        model.addAttribute("nombreCategoria", nombreCategoria);
        model.addAttribute("productos", productosCategoria);
        model.addAttribute("productosDisponibles", productosDeLaCategoria);

        return "almacenes/categoria";
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
    public static class CategoriaDTO {
        private String nombre;
        private Integer cantidad;
        private Double valor;

        public CategoriaDTO(String nombre, Integer cantidad, Double valor) {
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.valor = valor;
        }

        public String getNombre() {
            return nombre;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public Double getValor() {
            return valor;
        }
    }

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
