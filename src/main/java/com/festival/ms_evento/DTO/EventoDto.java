package com.festival.ms_evento.DTO;

import com.festival.ms_evento.model.EstadoEvento;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventoDto {

    @NotBlank(message = "El nombre del evento es obligatorio")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "La fecha del evento es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La hora del evento es obligatoria")
    private LocalTime hora;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    private Integer capacidad;

    @PositiveOrZero(message = "El precio no puede ser negativo")
    private Double precioEntrada;

    @NotNull(message = "El estado del evento es obligatorio")
    private EstadoEvento estado;
}