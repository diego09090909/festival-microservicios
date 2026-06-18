package com.festival.ms_logistica.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

import com.festival.ms_logistica.dto.ZonaDTO;
import com.festival.ms_logistica.service.ZonaService;

@WebMvcTest(ZonaController.class)
class ZonaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ZonaService zonaService;

    @Test
    @DisplayName("POST /api/zonas/ debe retornar 201 al crear zona")
    void crearZonaDebeRetornarCreated() throws Exception {

        ZonaDTO dto = new ZonaDTO();
        dto.setNombre("Zona Primeros Auxilios");
        dto.setTipo("SALUD");
        dto.setEventoId(1L);
        dto.setEventoNombre("Festival Reggae 2025");

        ZonaDTO guardada = new ZonaDTO();
        guardada.setId(1L);
        guardada.setNombre("Zona Primeros Auxilios");
        guardada.setTipo("SALUD");
        guardada.setEventoId(1L);

        when(zonaService.crearZona(any(ZonaDTO.class)))
                .thenReturn(guardada);

        mockMvc.perform(post("/api/zonas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Zona Primeros Auxilios"))
                .andExpect(jsonPath("$.tipo").value("SALUD"))
                .andExpect(jsonPath("$.eventoId").value(1L));
    }

    @Test
    @DisplayName("PUT /api/zonas/{id} debe retornar 200 al actualizar zona")
    void actualizarZonaDebeRetornarOk() throws Exception {

        ZonaDTO dto = new ZonaDTO();
        dto.setNombre("Zona Primeros Auxilios");
        dto.setTipo("SALUD");
        dto.setEventoId(1L);
        dto.setEventoNombre("Festival Reggae 2025");

        ZonaDTO guardada = new ZonaDTO();
        guardada.setId(1L);
        guardada.setNombre("Zona Primeros Auxilios");
        guardada.setTipo("SALUD");
        guardada.setEventoId(1L);

        when(zonaService.actualizarZona(eq(1L), any(ZonaDTO.class))) //
                .thenReturn(guardada);

        mockMvc.perform(put("/api/zonas/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Zona Primeros Auxilios"))
                .andExpect(jsonPath("$.tipo").value("SALUD"))
                .andExpect(jsonPath("$.eventoId").value(1L));
    }

    @Test
    @DisplayName("GET /api/zonas/evento/{eventoId} debe retornar 200 y lista vacia")
    void listarZonasPorEventoListaVacia() throws Exception {

        Long eventoId = 99L;
        when(zonaService.listaZonasPorEvento(eventoId))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/zonas/evento/{eventoId}", eventoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(zonaService).listaZonasPorEvento(eventoId);

    }

    @Test
    @DisplayName("GET /api/zonas/evento/{eventoId}/sinstaff debe retornar 200 y lista vacia")
    void listarZonasSinStaffListaVacia() throws Exception {

        Long eventoId = 99L;
        when(zonaService.listaZonasSinStaff(eventoId))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/zonas/evento/{eventoId}/sinstaff", eventoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(zonaService).listaZonasSinStaff(eventoId);
    }

    @Test
    @DisplayName("DELETE /api/zonas/{id} debe retornar 204 al eliminar")
    void eliminarZonaDebeRetornarNoContent() throws Exception {

        Long id = 1L;
        doNothing().when(zonaService).eliminarZona(id);

        mockMvc.perform(delete("/api/zonas/{id}", id))
                .andExpect(status().isNoContent());

        verify(zonaService).eliminarZona(id);
    }

}
