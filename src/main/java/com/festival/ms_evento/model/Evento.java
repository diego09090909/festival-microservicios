package com.festival.ms_evento.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "evento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false, length = 200)
    private String ubicacion;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    // Aforo maximo: regla de negocio critica para MS-Tickets
    @Column(nullable = false)
    private Integer aforoMaximo;

    // Estado actual del evento (maquina de estados)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEvento estado = EstadoEvento.BORRADOR;

    // Fecha de creacion del registro
    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();
}