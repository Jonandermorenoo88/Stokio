package com.proyecto.stockio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "almacenes")
public class Almacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre; // nombre almacen

    private String ubicacion; // ubicación

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto; // producto

    private Double precio; // precio (especifico del almacén?)

    private Integer cantidad; // cantidad

    public Almacen() {
    }

    public Almacen(String nombre, String ubicacion, Producto producto, Double precio, Integer cantidad) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.producto = producto;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
