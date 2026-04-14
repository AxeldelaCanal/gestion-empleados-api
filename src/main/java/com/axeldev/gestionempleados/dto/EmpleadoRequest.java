package com.axeldev.gestionempleados.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmpleadoRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        String apellido,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        String email,

        @NotBlank(message = "El departamento es obligatorio")
        String departamento,

        @NotNull(message = "El salario es obligatorio")
        @Positive(message = "El salario debe ser mayor a cero")
        BigDecimal salario,

        @NotNull(message = "La fecha de ingreso es obligatoria")
        LocalDate fechaIngreso
) {}
