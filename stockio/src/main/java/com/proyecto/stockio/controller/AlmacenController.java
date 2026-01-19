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

    @Autowired
    private com.proyecto.stockio.service.UsuarioService usuarioService;

    // Vista principal: Lista de almacenes con totales
    @GetMapping
    public String listarAlmacenes(Model model) {
        List<Almacen> almacenes = almacenRepository.findAll();
        List<AlmacenDTO> almacenesDTO = almacenes.stream().map(almacen -> {
            List<Inventario> inventarios = inventarioRepository
                    .findByAlmacen(java.util.Objects.requireNonNull(almacen));

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
        java.util.Objects.requireNonNull(id, "El ID del almacén no puede ser nulo");
        Almacen almacen = almacenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));

        List<Inventario> inventario = inventarioRepository.findByAlmacen(almacen);

        // Obtener solo las categorías asociadas a este almacén
        List<Categoria> todasLasCategorias = categoriaRepository.findByAlmacen(almacen);

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

            return new CategoriaDTO(cat.getId(), cat.getNombre(), cantidadTotal, valorTotal);
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
        java.util.Objects.requireNonNull(id, "El ID no puede ser nulo");
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
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'JEFE_ALMACEN')")
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("almacen", new Almacen());
        return "almacenes/form";
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'JEFE_ALMACEN')")
    @PostMapping("/guardar")
    public String guardarAlmacen(@ModelAttribute Almacen almacen) {
        almacenRepository.save(java.util.Objects.requireNonNull(almacen, "El almacén no puede ser nulo"));
        return "redirect:/almacenes";
    }

    // Editar almacén
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'JEFE_ALMACEN')")
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        java.util.Objects.requireNonNull(id, "El ID no puede ser nulo");
        Almacen almacen = almacenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("almacen", almacen);
        return "almacenes/form";
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'JEFE_ALMACEN')")
    @GetMapping("/eliminar/{id}")
    public String eliminarAlmacen(@PathVariable Long id) {
        // Opcional: Borrar inventario asociado primero o tener CascadeType.REMOVE en la
        // entidad (no hecho aqui por simplicidad)
        java.util.Objects.requireNonNull(id, "El ID no puede ser nulo");
        almacenRepository.deleteById(id);
        return "redirect:/almacenes";
    }

    // Añadir stock (producto) a un almacén
    @PostMapping("/{almacenId}/agregarProducto")
    public String agregarProducto(@PathVariable Long almacenId,
            @RequestParam Long productoId,
            @RequestParam Integer cantidad,
            java.security.Principal principal) {

        java.util.Objects.requireNonNull(almacenId, "El ID de almacén no puede ser nulo");
        Almacen almacen = almacenRepository.findById(almacenId).orElseThrow();
        java.util.Objects.requireNonNull(productoId, "El ID de producto no puede ser nulo");
        Producto producto = productoService.obtenerPorId(productoId);
        if (producto == null)
            throw new IllegalArgumentException("Producto no encontrado");

        // Obtener usuario
        com.proyecto.stockio.model.Usuario usuario = null;
        if (principal != null) {
            usuario = com.proyecto.stockio.service.UsuarioService.obtenerUsuarioDesdePrincipal(principal,
                    usuarioService);
        }

        // Usar AlbaranService para registrar la entrada y el movimiento
        albaranService.registrarEntrada(java.util.Objects.requireNonNull(almacen), producto, cantidad, usuario);

        return "redirect:/almacenes/" + almacenId;
    }

    // DTO simple para la vista
    public static class CategoriaDTO {
        private Long id; // Added ID field
        private String nombre;
        private Integer cantidad;
        private Double valor;

        public CategoriaDTO(Long id, String nombre, Integer cantidad, Double valor) {
            this.id = id;
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.valor = valor;
        }

        public Long getId() {
            return id;
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
