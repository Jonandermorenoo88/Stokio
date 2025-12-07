package com.proyecto.stockio;

import com.proyecto.stockio.model.Role;
import com.proyecto.stockio.model.Usuario;
import com.proyecto.stockio.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class StockioApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockioApplication.class, args);
    }

    // Inicializa un usuario ADMIN con contraseña codificada
    @Bean
    public CommandLineRunner initAdmin(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@stockio.com";
            if (!usuarioRepository.existsByEmail(adminEmail)) {
                Usuario admin = new Usuario();
                admin.setEmail(adminEmail);
                admin.setNombre("Administrador");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRol(Set.of(Role.ADMIN));
                usuarioRepository.save(admin);
                System.out.println("✅ ADMIN creado: " + adminEmail + " / admin123 (CODIFICADA)");
            } else {
                Usuario admin = usuarioRepository.findByEmail(adminEmail).get();
                admin.setPassword(passwordEncoder.encode("admin123"));
                usuarioRepository.save(admin);
                System.out.println("ℹ️ ADMIN actualizado: " + adminEmail + " / admin123 (CODIFICADA)");
            }
        };
    }
}
