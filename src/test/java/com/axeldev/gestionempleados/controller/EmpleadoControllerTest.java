package com.axeldev.gestionempleados.controller;

import com.axeldev.gestionempleados.dto.EmpleadoRequest;
import com.axeldev.gestionempleados.model.Empleado;
import com.axeldev.gestionempleados.repository.EmpleadoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("EmpleadoController — Integration Tests")
class EmpleadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmpleadoRepository repository;

    private Empleado empleadoGuardado;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        empleadoGuardado = repository.save(Empleado.builder()
                .nombre("María")
                .apellido("López")
                .email("maria.lopez@empresa.com")
                .departamento("Backend")
                .salario(new BigDecimal("180000"))
                .fechaIngreso(LocalDate.of(2022, 6, 15))
                .build());
    }

    // ── GET /api/empleados ────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/empleados: retorna 200 con página de empleados")
    void getAll_returns200() throws Exception {
        mockMvc.perform(get("/api/empleados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].email", is("maria.lopez@empresa.com")));
    }

    @Test
    @DisplayName("GET /api/empleados?nombre=María: filtra por nombre correctamente")
    void getAll_filtersByNombre() throws Exception {
        mockMvc.perform(get("/api/empleados").param("nombre", "María"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nombre", is("María")));
    }

    @Test
    @DisplayName("GET /api/empleados?departamento=NoExiste: retorna página vacía")
    void getAll_returnsEmptyWhenNoMatch() throws Exception {
        mockMvc.perform(get("/api/empleados").param("departamento", "NoExiste"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    // ── GET /api/empleados/{id} ───────────────────────────────────────────

    @Test
    @DisplayName("GET /api/empleados/{id}: retorna 200 cuando existe")
    void getById_returns200() throws Exception {
        mockMvc.perform(get("/api/empleados/{id}", empleadoGuardado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(empleadoGuardado.getId().intValue())))
                .andExpect(jsonPath("$.departamento", is("Backend")));
    }

    @Test
    @DisplayName("GET /api/empleados/{id}: retorna 404 cuando no existe")
    void getById_returns404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/empleados/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    // ── POST /api/empleados ───────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/empleados: retorna 201 con empleado creado")
    void create_returns201() throws Exception {
        EmpleadoRequest request = new EmpleadoRequest(
                "Carlos", "Ruiz", "carlos.ruiz@empresa.com",
                "DevOps", new BigDecimal("160000"), LocalDate.of(2024, 1, 10)
        );

        mockMvc.perform(post("/api/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.email", is("carlos.ruiz@empresa.com")))
                .andExpect(jsonPath("$.activo", is(true)));
    }

    @Test
    @DisplayName("POST /api/empleados: retorna 400 cuando el body es inválido")
    void create_returns400WhenInvalid() throws Exception {
        EmpleadoRequest invalidRequest = new EmpleadoRequest(
                "", "", "no-es-un-email", "", null, null
        );

        mockMvc.perform(post("/api/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.fieldErrors", notNullValue()));
    }

    // ── PUT /api/empleados/{id} ───────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/empleados/{id}: retorna 200 con empleado actualizado")
    void update_returns200() throws Exception {
        EmpleadoRequest request = new EmpleadoRequest(
                "María", "López", "maria.lopez@empresa.com",
                "Arquitectura", new BigDecimal("220000"), LocalDate.of(2022, 6, 15)
        );

        mockMvc.perform(put("/api/empleados/{id}", empleadoGuardado.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departamento", is("Arquitectura")))
                .andExpect(jsonPath("$.salario", is(220000)));
    }

    @Test
    @DisplayName("PUT /api/empleados/{id}: retorna 404 cuando no existe")
    void update_returns404WhenNotFound() throws Exception {
        EmpleadoRequest request = new EmpleadoRequest(
                "X", "Y", "x@y.com", "IT", new BigDecimal("100000"), LocalDate.now()
        );

        mockMvc.perform(put("/api/empleados/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/empleados/{id} ────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/empleados/{id}: retorna 204 cuando existe")
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/empleados/{id}", empleadoGuardado.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/empleados/{id}: retorna 404 cuando no existe")
    void delete_returns404WhenNotFound() throws Exception {
        mockMvc.perform(delete("/api/empleados/{id}", 9999L))
                .andExpect(status().isNotFound());
    }
}
