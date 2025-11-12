package com.proyecto.stockio;

import com.proyecto.stockio.model.Role;
import com.proyecto.stockio.model.Usuario;
import com.proyecto.stockio.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Set;

@SpringBootApplication
public class StockioApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockioApplication.class, args);
    }

    // Inicializa un usuario ADMIN sin codificar la contraseña (temporal)
    @Bean
    public CommandLineRunner initAdmin(UsuarioRepository usuarioRepository) {
        return args -> {
            String adminEmail = "admin@stockio.com";
            if (!usuarioRepository.existsByEmail(adminEmail)) {
                Usuario admin = new Usuario();
                admin.setEmail(adminEmail);
                admin.setNombre("Administrador");
                admin.setPassword("admin123");
                admin.setRoles(Set.of(Role.ADMIN));
                usuarioRepository.save(admin);
                System.out.println("✅ ADMIN creado: " + adminEmail + " / admin123 (SIN CODIFICAR)");
            } else {
                System.out.println("ℹ️ ADMIN ya existente: " + adminEmail);
            }
        };
    }
}
