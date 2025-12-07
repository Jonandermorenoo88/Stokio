package com.proyecto.stockio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "albaranes")
public class Albaran {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String codigo;

    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    private TipoAlbaran tipo;

    @ManyToOne
    @JoinColumn(name = "almacen_id")
    private Almacen almacen;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; // Usuario que realizó el movimiento

    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }

    public Albaran() {
    }

    public Albaran(String codigo, TipoAlbaran tipo, Almacen almacen, Usuario usuario) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.almacen = almacen;
        this.usuario = usuario;
        this.fecha = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public TipoAlbaran getTipo() {
        return tipo;
    }

    public void setTipo(TipoAlbaran tipo) {
        this.tipo = tipo;
    }

    public Almacen getAlmacen() {
        return almacen;
    }

    public void setAlmacen(Almacen almacen) {
        this.almacen = almacen;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
