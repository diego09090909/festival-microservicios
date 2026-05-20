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

    private static final Logger log =
            LoggerFactory.getLogger(EventoService.class);

    private final EventoRepository eventoRepository;
    private final EventoMapper eventoMapper;
    private final NotificacionClient notificacionClient;

    // LISTAR TODOS LOS EVENTOS
    public List<EventoDto> listarTodos() {

        log.info("Listando todos los eventos");

        return eventoRepository.findAll()
                .stream()
                .map(eventoMapper::toDto)
                .collect(Collectors.toList());
    }

    // LISTAR EVENTOS PUBLICADOS Y VIGENTES
    public List<EventoDto> listarPublicados() {

        log.info("Listando eventos publicados");

        return eventoRepository.findEventosActivosDesde(LocalDate.now())
                .stream()
                .map(eventoMapper::toDto)
                .collect(Collectors.toList());
    }

    // BUSCAR EVENTO POR ID
    public EventoDto buscarPorId(Long id) {

        log.info("Buscando evento con ID: {}", id);

        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Evento no encontrado: {}", id);

                    return new RuntimeException(
                            "Evento no encontrado con ID: " + id
                    );
                });

        return eventoMapper.toDto(evento);
    }

    // CREAR EVENTO
    public EventoDto crear(EventoDto dto) {

        log.info("Creando evento: {}", dto.getNombre());

        // VALIDAR FECHAS
        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) {

            log.error("Fecha fin inválida");

            throw new RuntimeException(
                    "La fecha de fin no puede ser anterior a la fecha de inicio"
            );
        }

        Evento evento = eventoMapper.toEntity(dto);

        Evento guardado = eventoRepository.save(evento);

        log.info("Evento creado con ID: {}", guardado.getId());

        return eventoMapper.toDto(guardado);
    }

    // ACTUALIZAR EVENTO
    public EventoDto actualizar(Long id, EventoDto dto) {

        log.info("Actualizando evento con ID: {}", id);

        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Evento no encontrado con ID: " + id
                ));

        // VALIDAR ESTADO
        if (evento.getEstado() == EstadoEvento.CANCELADO ||
                evento.getEstado() == EstadoEvento.FINALIZADO) {

            log.warn("No se puede editar evento en estado: {}",
                    evento.getEstado());

            throw new RuntimeException(
                    "No se puede editar un evento "
                            + evento.getEstado()
            );
        }

        // ACTUALIZAR DATOS
        evento.setNombre(dto.getNombre());
        evento.setDescripcion(dto.getDescripcion());
        evento.setUbicacion(dto.getUbicacion());
        evento.setFechaInicio(dto.getFechaInicio());
        evento.setFechaFin(dto.getFechaFin());
        evento.setAforoMaximo(dto.getAforoMaximo());

        Evento actualizado = eventoRepository.save(evento);

        log.info("Evento actualizado correctamente ID: {}", id);

        return eventoMapper.toDto(actualizado);
    }

    // CAMBIAR ESTADO DEL EVENTO
    public EventoDto cambiarEstado(Long id, String nuevoEstado) {

        log.info("Cambiando estado del evento {} a {}", id, nuevoEstado);

        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Evento no encontrado con ID: " + id
                ));

        EstadoEvento estadoActual = evento.getEstado();
        EstadoEvento estadoNuevo;

        // VALIDAR ESTADO
        try {

            estadoNuevo =
                    EstadoEvento.valueOf(nuevoEstado.toUpperCase());

        } catch (IllegalArgumentException e) {

            log.error("Estado inválido: {}", nuevoEstado);

            throw new RuntimeException(
                    "Estado inválido: " + nuevoEstado
            );
        }

        // VALIDAR TRANSICIÓN
        validarTransicion(estadoActual, estadoNuevo);

        evento.setEstado(estadoNuevo);

        Evento actualizado = eventoRepository.save(evento);

        log.info("Estado actualizado: {} -> {}",
                estadoActual,
                estadoNuevo);

        // NOTIFICACIONES ENTRE MICROSERVICIOS
        enviarNotificacion(actualizado, estadoNuevo);

        return eventoMapper.toDto(actualizado);
    }

    // VALIDAR TRANSICIÓN DE ESTADOS
    private void validarTransicion(
            EstadoEvento actual,
            EstadoEvento nuevo) {

        boolean valido = switch (actual) {

            case BORRADOR ->
                    nuevo == EstadoEvento.PUBLICADO
                            || nuevo == EstadoEvento.CANCELADO;

            case PUBLICADO ->
                    nuevo == EstadoEvento.CANCELADO
                            || nuevo == EstadoEvento.FINALIZADO;

            case CANCELADO, FINALIZADO -> false;
        };

        if (!valido) {

            log.error("Transición inválida: {} -> {}", actual, nuevo);

            throw new RuntimeException(
                    "Transición inválida: no se puede pasar de "
                            + actual + " a " + nuevo
            );
        }
    }

    // ENVIAR NOTIFICACIONES
    private void enviarNotificacion(
            Evento evento,
            EstadoEvento estado) {

        if (estado == EstadoEvento.CANCELADO) {

            notificacionClient.enviarNotificacion(
                    new NotificacionDto(
                            evento.getId(),
                            "EVENTO_CANCELADO",
                            "El evento '" + evento.getNombre()
                                    + "' ha sido cancelado."
                    )
            );

        } else if (estado == EstadoEvento.PUBLICADO) {

            notificacionClient.enviarNotificacion(
                    new NotificacionDto(
                            evento.getId(),
                            "EVENTO_PUBLICADO",
                            "El evento '" + evento.getNombre()
                                    + "' ya está disponible."
                    )
            );

        } else if (estado == EstadoEvento.FINALIZADO) {

            notificacionClient.enviarNotificacion(
                    new NotificacionDto(
                            evento.getId(),
                            "EVENTO_FINALIZADO",
                            "El evento '" + evento.getNombre()
                                    + "' ha finalizado."
                    )
            );
        }
    }

    // VALIDAR SI EVENTO ESTÁ PUBLICADO
    public boolean isEventoPublicado(Long id) {

        log.info("Validando si evento {} está publicado", id);

        return eventoRepository.existsByIdAndEstado(
                id,
                EstadoEvento.PUBLICADO
        );
    }
}