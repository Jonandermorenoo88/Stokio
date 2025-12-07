package com.proyecto.stockio.service;

import com.proyecto.stockio.model.Almacen;
import com.proyecto.stockio.model.Producto;
import com.proyecto.stockio.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Producto guardarProducto(Producto producto) {
        if (producto != null) {
            return productoRepository.save(producto);
        }
        return null;
    }

    public void eliminarProducto(Long id) {
        if (id != null) {
            productoRepository.deleteById(id);
        }
    }

    public Producto obtenerPorId(Long id) {
        if (id == null)
            return null;
        return productoRepository.findById(id).orElse(null);
    }

    public Optional<Producto> buscarPorId(Long id) {
        if (id == null)
            return Optional.empty();
        return productoRepository.findById(id);
    }

    /**
     * Devuelve productos que están disponibles para un almacén específico.
     * Criterio:
     * 1. Ubicación vacía o nula (Global)
     * 2. Ubicación coincide con nombre o ubicación del almacén
     */
    public List<Producto> encontrarDisponiblesParaAlmacen(Almacen almacen) {
        List<Producto> todos = productoRepository.findAll();

        return todos.stream()
                .filter(p -> esProductoVisibleEnAlmacen(p, almacen))
                .collect(Collectors.toList());
    }

    private boolean esProductoVisibleEnAlmacen(Producto p, Almacen a) {
        String prodUbicacion = p.getUbicacion();

        // Si no tiene ubicación definida, es global (visible en todos)
        if (prodUbicacion == null || prodUbicacion.trim().isEmpty()) {
            return true;
        }

        // Si tiene ubicación, debe coincidir con nombre o ubicación del almacén
        // Ignoramos mayúsculas/minúsculas
        return prodUbicacion.equalsIgnoreCase(a.getNombre()) ||
                prodUbicacion.equalsIgnoreCase(a.getUbicacion());
    }
}
