package com.festival.ms_evento.controller;

import com.festival.ms_evento.dto.EventoDto;
import com.festival.ms_evento.service.EventoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Eventos", description = "Gestión de eventos del festival")
@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
public class EventoController {

    private static final Logger log =
            LoggerFactory.getLogger(EventoController.class);

    private final EventoService eventoService;

    // LISTAR TODOS LOS EVENTOS
    @Operation(summary = "Listar todos los eventos")
    @GetMapping
    public ResponseEntity<List<EventoDto>> listar() {

        log.info("GET /api/eventos");

        return ResponseEntity.ok(eventoService.listarTodos());
    }

    // LISTAR EVENTOS PUBLICADOS
    @Operation(summary = "Listar eventos publicados")
    @GetMapping("/publicados")
    public ResponseEntity<List<EventoDto>> listarPublicados() {

        log.info("GET /api/eventos/publicados");

        return ResponseEntity.ok(eventoService.listarPublicados());
    }

    // BUSCAR EVENTO POR ID
    @Operation(summary = "Buscar evento por ID")
    @GetMapping("/{id}")
    public ResponseEntity<EventoDto> buscarPorId(@PathVariable Long id) {

        log.info("GET /api/eventos/{}", id);

        return ResponseEntity.ok(eventoService.buscarPorId(id));
    }

    // CREAR EVENTO
    @Operation(summary = "Crear un nuevo evento")
    @PostMapping
    public ResponseEntity<EventoDto> crear(
            @Valid @RequestBody EventoDto dto) {

        log.info("POST /api/eventos - nombre: {}", dto.getNombre());

        EventoDto creado = eventoService.crear(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // ACTUALIZAR EVENTO
    @Operation(summary = "Actualizar un evento")
    @PutMapping("/{id}")
    public ResponseEntity<EventoDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EventoDto dto) {

        log.info("PUT /api/eventos/{}", id);

        return ResponseEntity.ok(eventoService.actualizar(id, dto));
    }

    // CAMBIAR ESTADO DEL EVENTO
    @Operation(summary = "Cambiar estado del evento")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<EventoDto> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String nuevoEstado) {

        log.info("PATCH /api/eventos/{}/estado -> {}", id, nuevoEstado);

        return ResponseEntity.ok(
                eventoService.cambiarEstado(id, nuevoEstado)
        );
    }

    // VALIDAR SI EVENTO ESTÁ PUBLICADO
    @Operation(summary = "Verificar si un evento está publicado")
    @GetMapping("/{id}/publicado")
    public ResponseEntity<Boolean> isPublicado(@PathVariable Long id) {

        log.info("GET /api/eventos/{}/publicado", id);

        return ResponseEntity.ok(
                eventoService.isEventoPublicado(id)
        );
    }
}