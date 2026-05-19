package com.festival.ms_evento.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionDto {

    private Long eventoId;

    private String tipo;

    private String mensaje;
}