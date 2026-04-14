package com.axeldev.gestionempleados.repository;

import com.axeldev.gestionempleados.model.Empleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    @Query("SELECT e FROM Empleado e WHERE " +
           "(:nombre IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:departamento IS NULL OR LOWER(e.departamento) LIKE LOWER(CONCAT('%', :departamento, '%')))")
    Page<Empleado> findWithFilters(
            @Param("nombre") String nombre,
            @Param("departamento") String departamento,
            Pageable pageable
    );
}
