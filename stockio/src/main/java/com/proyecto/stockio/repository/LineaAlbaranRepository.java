package com.proyecto.stockio.repository;

import com.proyecto.stockio.model.Albaran;
import com.proyecto.stockio.model.LineaAlbaran;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LineaAlbaranRepository extends JpaRepository<LineaAlbaran, Long> {
    List<LineaAlbaran> findByAlbaran(Albaran albaran);
}
