package com.axeldev.gestionempleados.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "empleados")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String departamento;

    @Column(nullable = false)
    private BigDecimal salario;

    @Column(nullable = false)
    private LocalDate fechaIngreso;

    private boolean activo = true;

    public Empleado() {}

    private Empleado(Builder builder) {
        this.id = builder.id;
        this.nombre = builder.nombre;
        this.apellido = builder.apellido;
        this.email = builder.email;
        this.departamento = builder.departamento;
        this.salario = builder.salario;
        this.fechaIngreso = builder.fechaIngreso;
        this.activo = builder.activo;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String nombre;
        private String apellido;
        private String email;
        private String departamento;
        private BigDecimal salario;
        private LocalDate fechaIngreso;
        private boolean activo = true;

        public Builder id(Long id)                       { this.id = id; return this; }
        public Builder nombre(String nombre)             { this.nombre = nombre; return this; }
        public Builder apellido(String apellido)         { this.apellido = apellido; return this; }
        public Builder email(String email)               { this.email = email; return this; }
        public Builder departamento(String departamento) { this.departamento = departamento; return this; }
        public Builder salario(BigDecimal salario)       { this.salario = salario; return this; }
        public Builder fechaIngreso(LocalDate fecha)     { this.fechaIngreso = fecha; return this; }
        public Builder activo(boolean activo)            { this.activo = activo; return this; }
        public Empleado build()                          { return new Empleado(this); }
    }

    public Long getId()                  { return id; }
    public String getNombre()            { return nombre; }
    public String getApellido()          { return apellido; }
    public String getEmail()             { return email; }
    public String getDepartamento()      { return departamento; }
    public BigDecimal getSalario()       { return salario; }
    public LocalDate getFechaIngreso()   { return fechaIngreso; }
    public boolean isActivo()            { return activo; }

    public void setNombre(String nombre)             { this.nombre = nombre; }
    public void setApellido(String apellido)         { this.apellido = apellido; }
    public void setEmail(String email)               { this.email = email; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public void setSalario(BigDecimal salario)       { this.salario = salario; }
    public void setFechaIngreso(LocalDate fecha)     { this.fechaIngreso = fecha; }
    public void setActivo(boolean activo)            { this.activo = activo; }
}
