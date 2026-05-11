package com.festival.ms_evento.controller;

import com.festival.ms_evento.model.Evento;
import com.festival.ms_evento.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;

    @PostMapping
    public ResponseEntity<Evento> crear(@Valid @RequestBody Evento evento) {

        return ResponseEntity.status(201)
                .body(eventoService.crearEvento(evento));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> obtener(@PathVariable Long id) {

        return ResponseEntity.ok(eventoService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<Evento>> listar() {

        return ResponseEntity.ok(eventoService.listarEventos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> actualizar(@PathVariable Long id,
                                             @Valid @RequestBody Evento evento) {

        return ResponseEntity.ok(eventoService.actualizarEvento(id, evento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        eventoService.eliminarEvento(id);

        return ResponseEntity.noContent().build();
    }
}