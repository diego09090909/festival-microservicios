package com.festival.ms_logistica.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.festival.ms_logistica.dto.AsignacionStaffDTO;
import com.festival.ms_logistica.service.AsignacionStaffService;

@WebMvcTest(AsignacionStaffController.class)
class AsignacionStaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AsignacionStaffService asignacionStaffService;


    @Test
    @DisplayName("POST /api/asignaciones/{usuarioId} debe retornar 201 al asignar staff")
    void asignarStaffDebeRetornarCreated() throws Exception {

        Long usuarioId = 1L;

        AsignacionStaffDTO dto = new AsignacionStaffDTO();
        dto.setUsuarioId(usuarioId);
        dto.setZonaId(2L);
        dto.setLabor("Guardia");

        when(asignacionStaffService.asignarStaff(eq(usuarioId), any(AsignacionStaffDTO.class)))
            .thenReturn(dto);

        mockMvc.perform(post("/api/asignaciones/{usuarioId}", usuarioId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.labor").value("Guardia"));

        verify(asignacionStaffService).asignarStaff(eq(usuarioId), any(AsignacionStaffDTO.class));
    }


    @Test
    @DisplayName("PUT /api/asignaciones/{usuarioId} debe retornar 200 al actualizar staff")
    void actualizarStaffDebeRetornarOk() throws Exception {

        Long usuarioId = 1L;

        AsignacionStaffDTO dto = new AsignacionStaffDTO();
        dto.setUsuarioId(usuarioId);
        dto.setZonaId(3L);
        dto.setLabor("Control acceso");

        when(asignacionStaffService.actualizarAsignacion(eq(usuarioId), any(AsignacionStaffDTO.class)))
            .thenReturn(dto);

        mockMvc.perform(put("/api/asignaciones/{usuarioId}", usuarioId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.labor").value("Control acceso"));

        verify(asignacionStaffService).actualizarAsignacion(eq(usuarioId), any(AsignacionStaffDTO.class));
    }


    @Test
    @DisplayName("GET /api/asignaciones/staffZona/{zonaId} debe retornar 200 y lista")
    void listarStaffPorZonaDebeRetornarOkYLista() throws Exception {

        Long zonaId = 2L;

        AsignacionStaffDTO dto = new AsignacionStaffDTO();
        dto.setUsuarioId(1L);
        dto.setZonaId(zonaId);
        dto.setLabor("Vendedor");

        when(asignacionStaffService.ListaStaffPorZona(zonaId))
            .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/asignaciones/staffZona/{zonaId}", zonaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].labor").value("Vendedor"));

        verify(asignacionStaffService).ListaStaffPorZona(zonaId);
    }


    @Test
    @DisplayName("GET /api/asignaciones/staffZona/{zonaId} debe retornar 200 y lista vacía")
    void listarStaffPorZonaDebeRetornarListaVacia() throws Exception {

        Long zonaId = 99L;

        when(asignacionStaffService.ListaStaffPorZona(zonaId))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/asignaciones/staffZona/{zonaId}", zonaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(asignacionStaffService).ListaStaffPorZona(zonaId);
    }


    @Test
    @DisplayName("DELETE /api/asignaciones/{id} debe retornar 204 al eliminar")
    void eliminarAsignacionDebeRetornarNoContent() throws Exception {

        Long id = 1L;

        doNothing().when(asignacionStaffService).eliminarAsignacion(id);

        mockMvc.perform(delete("/api/asignaciones/{id}", id))
                .andExpect(status().isNoContent());

        verify(asignacionStaffService).eliminarAsignacion(id);
    }
}