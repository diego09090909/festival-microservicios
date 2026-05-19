package com.festival.ms_evento.service;

import com.festival.ms_evento.client.NotificacionClient;
import com.festival.ms_evento.dto.EventoDto;
import com.festival.ms_evento.dto.NotificacionDto;
import com.festival.ms_evento.mapper.EventoMapper;
import com.festival.ms_evento.model.EstadoEvento;
import com.festival.ms_evento.model.Evento;
import com.festival.ms_evento.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventoService {

    private static final Logger log = LoggerFactory.getLogger(EventoService.class);

    private final EventoRepository eventoRepository;
    private final EventoMapper eventoMapper;
    private final NotificacionClient notificacionClient; // ← AGREGADO

    public List<EventoDto> listarTodos() {
        log.info("Listando todos los eventos");
        return eventoRepository.findAll()
                .stream()
                .map(eventoMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<EventoDto> listarPublicados() {
        log.info("Listando eventos publicados vigentes");
        return eventoRepository.findEventosActivosDesde(LocalDate.now())
                .stream()
                .map(eventoMapper::toDto)
                .collect(Collectors.toList());
    }

    public EventoDto buscarPorId(Long id) {
        log.info("Buscando evento con ID: {}", id);
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Evento no encontrado: {}", id);
                    return new RuntimeException("Evento no encontrado con ID: " + id);
                });
        return eventoMapper.toDto(evento);
    }

    public EventoDto crear(EventoDto dto) {
        log.info("Creando evento: {}", dto.getNombre());

        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new RuntimeException("La fecha de fin no puede ser anterior a la de inicio");
        }

        Evento evento = eventoMapper.toEntity(dto);
        Evento guardado = eventoRepository.save(evento);
        log.info("Evento creado con ID: {}", guardado.getId());
        return eventoMapper.toDto(guardado);
    }

    public EventoDto actualizar(Long id, EventoDto dto) {
        log.info("Actualizando evento: {}", id);
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));

        if (evento.getEstado() == EstadoEvento.CANCELADO ||
            evento.getEstado() == EstadoEvento.FINALIZADO) {
            throw new RuntimeException("No se puede editar un evento " + evento.getEstado());
        }

        evento.setNombre(dto.getNombre());
        evento.setDescripcion(dto.getDescripcion());
        evento.setUbicacion(dto.getUbicacion());
        evento.setFechaInicio(dto.getFechaInicio());
        evento.setFechaFin(dto.getFechaFin());
        evento.setAforoMaximo(dto.getAforoMaximo());

        return eventoMapper.toDto(eventoRepository.save(evento));
    }

    public EventoDto cambiarEstado(Long id, String nuevoEstado) {
        log.info("Cambiando estado del evento {} a {}", id, nuevoEstado);
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));

        EstadoEvento estadoActual = evento.getEstado();
        EstadoEvento estadoNuevo;

        try {
            estadoNuevo = EstadoEvento.valueOf(nuevoEstado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado invalido: " + nuevoEstado);
        }

        validarTransicion(estadoActual, estadoNuevo);

        evento.setEstado(estadoNuevo);
        Evento actualizado = eventoRepository.save(evento);
        log.info("Estado cambiado exitosamente: {} -> {}", estadoActual, estadoNuevo);

        // ← AGREGADO: notifica a ms-notificaciones según el nuevo estado
        if (estadoNuevo == EstadoEvento.CANCELADO) {
            notificacionClient.enviarNotificacion(new NotificacionDto(
                actualizado.getId(),
                "EVENTO_CANCELADO",
                "El evento '" + actualizado.getNombre() + "' ha sido cancelado."
            ));
        } else if (estadoNuevo == EstadoEvento.PUBLICADO) {
            notificacionClient.enviarNotificacion(new NotificacionDto(
                actualizado.getId(),
                "EVENTO_PUBLICADO",
                "El evento '" + actualizado.getNombre() + "' ya esta disponible!"
            ));
        } else if (estadoNuevo == EstadoEvento.FINALIZADO) {
            notificacionClient.enviarNotificacion(new NotificacionDto(
                actualizado.getId(),
                "EVENTO_FINALIZADO",
                "El evento '" + actualizado.getNombre() + "' ha finalizado. Gracias por participar!"
            ));
        }

        return eventoMapper.toDto(actualizado);
    }

    private void validarTransicion(EstadoEvento actual, EstadoEvento nuevo) {
        boolean valido = switch (actual) {
            case BORRADOR  -> nuevo == EstadoEvento.PUBLICADO || nuevo == EstadoEvento.CANCELADO;
            case PUBLICADO -> nuevo == EstadoEvento.CANCELADO || nuevo == EstadoEvento.FINALIZADO;
            case CANCELADO, FINALIZADO -> false;
        };
        if (!valido) {
            throw new RuntimeException(
                "Transicion invalida: no se puede pasar de " + actual + " a " + nuevo);
        }
    }

    public boolean isEventoPublicado(Long id) {
        return eventoRepository.existsByIdAndEstado(id, EstadoEvento.PUBLICADO);
    }
}