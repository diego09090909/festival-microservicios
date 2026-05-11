package com.festival.ms_evento.repository;

import com.festival.ms_evento.model.EstadoEvento;
import com.festival.ms_evento.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByActivoTrue();

    List<Evento> findByEstado(EstadoEvento estado);

    List<Evento> findByNombreContainingIgnoreCase(String nombre);
}