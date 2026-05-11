package com.festival.ms_evento.service;

import com.festival.ms_evento.model.Evento;
import com.festival.ms_evento.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventoServiceImpl implements EventoService {

    private final EventoRepository eventoRepository;

    @Override
    public Evento crearEvento(Evento evento) {

        evento.setActivo(true);

        return eventoRepository.save(evento);
    }

    @Override
    public Evento obtenerPorId(Long id) {

        return eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
    }

    @Override
    public List<Evento> listarEventos() {

        return eventoRepository.findByActivoTrue();
    }

    @Override
    public Evento actualizarEvento(Long id, Evento evento) {

        Evento existente = obtenerPorId(id);

        existente.setNombre(evento.getNombre());
        existente.setDescripcion(evento.getDescripcion());
        existente.setFecha(evento.getFecha());
        existente.setHora(evento.getHora());
        existente.setUbicacion(evento.getUbicacion());
        existente.setCapacidad(evento.getCapacidad());
        existente.setPrecioEntrada(evento.getPrecioEntrada());
        existente.setEstado(evento.getEstado());

        return eventoRepository.save(existente);
    }

    @Override
    public void eliminarEvento(Long id) {

        Evento evento = obtenerPorId(id);

        evento.setActivo(false);

        eventoRepository.save(evento);
    }
}