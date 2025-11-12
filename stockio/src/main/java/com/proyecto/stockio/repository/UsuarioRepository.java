package com.proyecto.stockio.repository;

import com.proyecto.stockio.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar usuario por su email (para login o validaciones)
    Optional<Usuario> findByEmail(String email);

    // Saber si un email ya está registrado
    boolean existsByEmail(String email);
}
