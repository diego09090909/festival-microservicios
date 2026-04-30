package com.festival.ms_logistica.dto;

import com.festival.ms_logistica.model.Zona;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignacionStaffDTO {
    
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El nombre del usuario es obligatorio")
    private String usuarioNombre;

    @NotNull(message = "La zona es obligatorio")
    private Zona zona;

}
