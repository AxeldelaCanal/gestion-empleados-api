package com.axeldev.gestionempleados.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmpleadoResponse(
        Long id,
        String nombre,
        String apellido,
        String email,
        String departamento,
        BigDecimal salario,
        LocalDate fechaIngreso,
        boolean activo
) {}
