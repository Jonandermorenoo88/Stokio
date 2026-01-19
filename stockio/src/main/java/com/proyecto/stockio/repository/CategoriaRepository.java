package com.proyecto.stockio.repository;

import com.proyecto.stockio.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    java.util.List<Categoria> findByAlmacen(com.proyecto.stockio.model.Almacen almacen);
}
