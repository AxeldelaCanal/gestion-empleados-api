package com.axeldev.gestionempleados.service;

import com.axeldev.gestionempleados.dto.EmpleadoRequest;
import com.axeldev.gestionempleados.dto.EmpleadoResponse;
import com.axeldev.gestionempleados.exception.ResourceNotFoundException;
import com.axeldev.gestionempleados.model.Empleado;
import com.axeldev.gestionempleados.repository.EmpleadoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository repository;

    public EmpleadoServiceImpl(EmpleadoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<EmpleadoResponse> findAll(String nombre, String departamento, Pageable pageable) {
        return repository.findWithFilters(nombre, departamento, pageable)
                .map(this::toResponse);
    }

    @Override
    public EmpleadoResponse findById(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con id: " + id));
    }

    @Override
    @Transactional
    public EmpleadoResponse create(EmpleadoRequest request) {
        Empleado empleado = Empleado.builder()
                .nombre(request.nombre())
                .apellido(request.apellido())
                .email(request.email())
                .departamento(request.departamento())
                .salario(request.salario())
                .fechaIngreso(request.fechaIngreso())
                .build();
        return toResponse(repository.save(empleado));
    }

    @Override
    @Transactional
    public EmpleadoResponse update(Long id, EmpleadoRequest request) {
        Empleado empleado = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con id: " + id));

        empleado.setNombre(request.nombre());
        empleado.setApellido(request.apellido());
        empleado.setEmail(request.email());
        empleado.setDepartamento(request.departamento());
        empleado.setSalario(request.salario());
        empleado.setFechaIngreso(request.fechaIngreso());

        return toResponse(repository.save(empleado));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Empleado no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }

    private EmpleadoResponse toResponse(Empleado e) {
        return new EmpleadoResponse(
                e.getId(),
                e.getNombre(),
                e.getApellido(),
                e.getEmail(),
                e.getDepartamento(),
                e.getSalario(),
                e.getFechaIngreso(),
                e.isActivo()
        );
    }
}
