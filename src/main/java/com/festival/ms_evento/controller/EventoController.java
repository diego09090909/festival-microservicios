package com.festival.ms_evento.controller;

import com.festival.ms_evento.dto.EventoDto;
import com.festival.ms_evento.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
public class EventoController {

    private static final Logger log = LoggerFactory.getLogger(EventoController.class);
    private final EventoService eventoService;

    // GET /api/eventos → todos los eventos
    @GetMapping
    public ResponseEntity<List<EventoDto>> listar() {
        return ResponseEntity.ok(eventoService.listarTodos());
    }

    // GET /api/eventos/publicados → solo eventos disponibles para tickets
    @GetMapping("/publicados")
    public ResponseEntity<List<EventoDto>> listarPublicados() {
        return ResponseEntity.ok(eventoService.listarPublicados());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoDto> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/eventos/{}", id);
        return ResponseEntity.ok(eventoService.buscarPorId(id));
    }

    
    @PostMapping
    public ResponseEntity<EventoDto> crear(@Valid @RequestBody EventoDto dto) {
        log.info("POST /api/eventos - nombre: {}", dto.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.crear(dto));
    }

    // PUT /api/eventos/{id} → actualizar datos del evento
    @PutMapping("/{id}")
    public ResponseEntity<EventoDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EventoDto dto) {
        return ResponseEntity.ok(eventoService.actualizar(id, dto));
    }

    // PATCH /api/eventos/{id}/estado → cambia el estado del evento
    // Ejemplo: PATCH /api/eventos/1/estado?nuevoEstado=PUBLICADO
    @PatchMapping("/{id}/estado")
    public ResponseEntity<EventoDto> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String nuevoEstado) {
        log.info("PATCH /api/eventos/{}/estado -> {}", id, nuevoEstado);
        return ResponseEntity.ok(eventoService.cambiarEstado(id, nuevoEstado));
    }

    // GET /api/eventos/{id}/publicado → endpoint para Feign de otros MS
    // Devuelve true/false: MS-Tickets lo usa para validar antes de vender
    @GetMapping("/{id}/publicado")
    public ResponseEntity<Boolean> isPublicado(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.isEventoPublicado(id));
    }
}