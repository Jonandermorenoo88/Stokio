package com.proyecto.stockio.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String nombre;

    @Column(nullable = false)
    private String pasword;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> rol = new HashSet<>();

    // ====== Getters y Setters ======

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return pasword;
    }
    public void setPassword(String pasword) {
        this.pasword = pasword;
    }

    public Set<Role> getRol() {
        return rol;
    }
    public void setRol(Set<Role> roles) {
        this.rol = roles;
    }
}
