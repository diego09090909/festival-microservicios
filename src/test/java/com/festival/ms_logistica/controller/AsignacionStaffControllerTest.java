package com.festival.ms_logistica.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.festival.ms_logistica.dto.AsignacionStaffDTO;
import com.festival.ms_logistica.security.JwtUtil;
import com.festival.ms_logistica.service.AsignacionStaffService;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(AsignacionStaffController.class)
class AsignacionStaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AsignacionStaffService asignacionStaffService;

    @MockBean
    private JwtUtil jwtUtil;
    


    @Test
    @WithMockUser(roles = "ADMIN")
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
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.labor").value("Guardia"));

        verify(asignacionStaffService).asignarStaff(eq(usuarioId), any(AsignacionStaffDTO.class));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
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
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.labor").value("Control acceso"));

        verify(asignacionStaffService).actualizarAsignacion(eq(usuarioId), any(AsignacionStaffDTO.class));
    }


}