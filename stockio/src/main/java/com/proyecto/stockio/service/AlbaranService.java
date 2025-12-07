package com.proyecto.stockio.service;

import com.proyecto.stockio.model.*;
import com.proyecto.stockio.repository.AlbaranRepository;
import com.proyecto.stockio.repository.InventarioRepository;
import com.proyecto.stockio.repository.LineaAlbaranRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AlbaranService {

    @Autowired
    private AlbaranRepository albaranRepository;

    @Autowired
    private LineaAlbaranRepository lineaAlbaranRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    /**
     * Registra una entrada de stock generando el Albarán correspondiente
     * y actualizando el inventario.
     */
    @Transactional
    public void registrarEntrada(Almacen almacen, Producto producto, Integer cantidad, Usuario usuario) {
        // 1. Crear Albarán Header
        Albaran albaran = new Albaran();
        albaran.setCodigo("ENT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        albaran.setFecha(LocalDateTime.now());
        albaran.setTipo(TipoAlbaran.ENTRADA);
        albaran.setAlmacen(almacen);
        albaran.setUsuario(usuario);
        albaranRepository.save(albaran);

        // 2. Crear Linea de Albarán
        LineaAlbaran linea = new LineaAlbaran();
        linea.setAlbaran(albaran);
        linea.setProducto(producto);
        linea.setCantidad(cantidad);
        linea.setPrecioUnitario(producto.getPrecio()); // Precio snapshot
        lineaAlbaranRepository.save(linea);

        // 3. Actualizar Inventario Real
        Inventario inventario = inventarioRepository.findByAlmacenAndProducto(almacen, producto)
                .orElse(new Inventario(almacen, producto, 0));

        inventario.setCantidad(inventario.getCantidad() + cantidad);
        inventarioRepository.save(inventario);
    }

    /**
     * Registra una salida de stock (Venta) generando Albarán y actualizando
     * inventario.
     */
    @Transactional
    public void registrarSalida(Almacen almacen, Producto producto, Integer cantidad, Usuario usuario) {
        // 1. Validar stock
        Inventario inventario = inventarioRepository.findByAlmacenAndProducto(almacen, producto)
                .orElseThrow(() -> new IllegalArgumentException("No hay stock de este producto en el almacén"));

        if (inventario.getCantidad() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + inventario.getCantidad()); // TODO:
                                                                                                               // Manejar
                                                                                                               // mejor
                                                                                                               // la
                                                                                                               // excepción
                                                                                                               // en la
                                                                                                               // vista
        }

        // 2. Crear Albarán Header
        Albaran albaran = new Albaran();
        albaran.setCodigo("SAL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        albaran.setFecha(LocalDateTime.now());
        albaran.setTipo(TipoAlbaran.SALIDA);
        albaran.setAlmacen(almacen);
        albaran.setUsuario(usuario);
        albaranRepository.save(albaran);

        // 3. Crear Linea de Albarán
        LineaAlbaran linea = new LineaAlbaran();
        linea.setAlbaran(albaran);
        linea.setProducto(producto);
        linea.setCantidad(cantidad);
        linea.setPrecioUnitario(producto.getPrecio());
        lineaAlbaranRepository.save(linea);

        // 4. Actualizar Inventario Real
        inventario.setCantidad(inventario.getCantidad() - cantidad);
        inventarioRepository.save(inventario);
    }
}
