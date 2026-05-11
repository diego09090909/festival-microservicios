package com.festival.ms_evento.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "eventos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;

    private String descripcion;

    @NotNull
    private LocalDate fecha;

    @NotNull
    private LocalTime hora;

    @NotBlank
    private String ubicacion;

    @Min(1)
    private Integer capacidad;

    @PositiveOrZero
    private Double precioEntrada;

    @Enumerated(EnumType.STRING)
    private EstadoEvento estado;

    @Column(nullable = false)
    private Boolean activo;
}