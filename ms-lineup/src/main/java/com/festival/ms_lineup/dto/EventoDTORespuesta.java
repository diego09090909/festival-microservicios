package com.festival.ms_lineup.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventoDTORespuesta {

    private Long id;
    private String nombre;
    private String estado;
    private Integer capacidad;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
}