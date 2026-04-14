package com.axeldev.gestionempleados.service;

import com.axeldev.gestionempleados.dto.EmpleadoRequest;
import com.axeldev.gestionempleados.dto.EmpleadoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmpleadoService {

    Page<EmpleadoResponse> findAll(String nombre, String departamento, Pageable pageable);

    EmpleadoResponse findById(Long id);

    EmpleadoResponse create(EmpleadoRequest request);

    EmpleadoResponse update(Long id, EmpleadoRequest request);

    void delete(Long id);
}
