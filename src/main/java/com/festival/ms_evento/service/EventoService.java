package com.festival.ms_evento.service;

import com.festival.ms_evento.model.Evento;

import java.util.List;

public interface EventoService {

    Evento crearEvento(Evento evento);

    Evento obtenerPorId(Long id);

    List<Evento> listarEventos();

    Evento actualizarEvento(Long id, Evento evento);

    void eliminarEvento(Long id);
}