package com.proyecto.stockio.repository;

import com.proyecto.stockio.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @org.springframework.data.jpa.repository.Query("SELECT p FROM Producto p WHERE (p.ubicacion IS NULL OR trim(p.ubicacion) = '') OR lower(p.ubicacion) = lower(:nombreAlmacen) OR lower(p.ubicacion) = lower(:ubicacionAlmacen)")
    java.util.List<Producto> findDisponiblesParaAlmacen(
            @org.springframework.data.repository.query.Param("nombreAlmacen") String nombreAlmacen,
            @org.springframework.data.repository.query.Param("ubicacionAlmacen") String ubicacionAlmacen);
}
