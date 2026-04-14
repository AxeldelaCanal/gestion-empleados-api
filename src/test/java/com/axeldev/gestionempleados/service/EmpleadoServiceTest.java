package com.axeldev.gestionempleados.service;

import com.axeldev.gestionempleados.dto.EmpleadoRequest;
import com.axeldev.gestionempleados.dto.EmpleadoResponse;
import com.axeldev.gestionempleados.exception.ResourceNotFoundException;
import com.axeldev.gestionempleados.model.Empleado;
import com.axeldev.gestionempleados.repository.EmpleadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmpleadoService — Unit Tests")
class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository repository;

    @InjectMocks
    private EmpleadoServiceImpl service;

    private Empleado empleado;
    private EmpleadoRequest request;

    @BeforeEach
    void setUp() {
        empleado = Empleado.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("García")
                .email("juan.garcia@empresa.com")
                .departamento("Ingeniería")
                .salario(new BigDecimal("150000"))
                .fechaIngreso(LocalDate.of(2023, 3, 1))
                .activo(true)
                .build();

        request = new EmpleadoRequest(
                "Juan",
                "García",
                "juan.garcia@empresa.com",
                "Ingeniería",
                new BigDecimal("150000"),
                LocalDate.of(2023, 3, 1)
        );
    }

    // ── findAll ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAll: retorna página de empleados correctamente")
    void findAll_returnsPageOfEmployees() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Empleado> page = new PageImpl<>(List.of(empleado));
        when(repository.findWithFilters(null, null, pageable)).thenReturn(page);

        Page<EmpleadoResponse> result = service.findAll(null, null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).email()).isEqualTo("juan.garcia@empresa.com");
        verify(repository).findWithFilters(null, null, pageable);
    }

    @Test
    @DisplayName("findAll: retorna página vacía cuando no hay resultados")
    void findAll_returnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findWithFilters(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(Page.empty());

        Page<EmpleadoResponse> result = service.findAll("Nadie", "NoDept", pageable);

        assertThat(result.getTotalElements()).isZero();
    }

    // ── findById ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById: retorna empleado cuando existe")
    void findById_returnsEmployeeWhenExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(empleado));

        EmpleadoResponse result = service.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.nombre()).isEqualTo("Juan");
        assertThat(result.departamento()).isEqualTo("Ingeniería");
    }

    @Test
    @DisplayName("findById: lanza ResourceNotFoundException cuando no existe")
    void findById_throwsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── create ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("create: guarda y retorna el empleado creado")
    void create_savesAndReturnsEmployee() {
        when(repository.save(any(Empleado.class))).thenReturn(empleado);

        EmpleadoResponse result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("juan.garcia@empresa.com");
        assertThat(result.activo()).isTrue();
        verify(repository).save(any(Empleado.class));
    }

    // ── update ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("update: actualiza y retorna el empleado modificado")
    void update_updatesAndReturnsEmployee() {
        EmpleadoRequest updateRequest = new EmpleadoRequest(
                "Juan", "García", "juan.garcia@empresa.com",
                "Arquitectura", new BigDecimal("200000"), LocalDate.of(2023, 3, 1)
        );
        Empleado updated = Empleado.builder()
                .id(1L).nombre("Juan").apellido("García")
                .email("juan.garcia@empresa.com").departamento("Arquitectura")
                .salario(new BigDecimal("200000")).fechaIngreso(LocalDate.of(2023, 3, 1))
                .activo(true).build();

        when(repository.findById(1L)).thenReturn(Optional.of(empleado));
        when(repository.save(any(Empleado.class))).thenReturn(updated);

        EmpleadoResponse result = service.update(1L, updateRequest);

        assertThat(result.departamento()).isEqualTo("Arquitectura");
        assertThat(result.salario()).isEqualByComparingTo("200000");
    }

    @Test
    @DisplayName("update: lanza ResourceNotFoundException cuando no existe")
    void update_throwsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository, never()).save(any());
    }

    // ── delete ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete: elimina el empleado cuando existe")
    void delete_deletesWhenExists() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        assertThatCode(() -> service.delete(1L)).doesNotThrowAnyException();

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("delete: lanza ResourceNotFoundException cuando no existe")
    void delete_throwsWhenNotFound() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository, never()).deleteById(any());
    }
}
