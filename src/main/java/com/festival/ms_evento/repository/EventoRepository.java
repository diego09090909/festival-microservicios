package com.festival.ms_evento.repository;

import com.festival.ms_evento.model.Evento;
import com.festival.ms_evento.model.EstadoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    // Query Method: filtra eventos por estado
    List<Evento> findByEstado(EstadoEvento estado);

    // Query Method: eventos publicados (los que pueden vender tickets)
    List<Evento> findByEstadoOrderByFechaInicioAsc(EstadoEvento estado);

    // Query Method: busca por ubicacion (para busquedas de asistentes)
    List<Evento> findByUbicacionContainingIgnoreCase(String ubicacion);

    // Custom Query: eventos publicados con aforo disponible
    // MS-Tickets usa esto para validar antes de vender entradas
    @Query("SELECT e FROM Evento e WHERE e.estado = 'PUBLICADO' AND e.fechaFin >= :hoy")
    List<Evento> findEventosActivosDesde(@Param("hoy") LocalDate hoy);

    // Verifica si un evento esta publicado (usado por Feign desde otros MS)
    boolean existsByIdAndEstado(Long id, EstadoEvento estado);
}