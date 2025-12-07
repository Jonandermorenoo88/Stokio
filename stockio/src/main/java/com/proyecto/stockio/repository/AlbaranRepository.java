package com.proyecto.stockio.repository;

import com.proyecto.stockio.model.Albaran;
import com.proyecto.stockio.model.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlbaranRepository extends JpaRepository<Albaran, Long> {
    List<Albaran> findByAlmacenOrderByFechaDesc(Almacen almacen);
}
