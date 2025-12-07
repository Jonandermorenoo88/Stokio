package com.proyecto.stockio.repository;

import com.proyecto.stockio.model.Almacen;
import com.proyecto.stockio.model.Inventario;
import com.proyecto.stockio.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    // Obtener todo el inventario de un almacén
    List<Inventario> findByAlmacen(Almacen almacen);

    // Buscar si existe un producto específico en un almacén
    Optional<Inventario> findByAlmacenAndProducto(Almacen almacen, Producto producto);
}
