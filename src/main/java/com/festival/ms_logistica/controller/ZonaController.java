package com.festival.ms_logistica.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.festival.ms_logistica.dto.ZonaDTO;
import com.festival.ms_logistica.service.ZonaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




@Tag(name = "Zonas", description = "Gestión de zonas del festival")
@RestController
@RequestMapping("/api/zonas")
@RequiredArgsConstructor
public class ZonaController {

    private final ZonaService zonaService;
    
    @Operation(summary = "Crear una nueva zona")
    @PostMapping
    public ResponseEntity<ZonaDTO> crearZona(@Valid @RequestBody ZonaDTO dto) {
        ZonaDTO zona = zonaService.crearZona(dto);
        
        return new ResponseEntity<>(zona, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar una zona")
    @PutMapping("/{id}")
    public ResponseEntity<ZonaDTO> actualizarZona(@PathVariable Long id, @Valid @RequestBody ZonaDTO dto) {
        ZonaDTO zona = zonaService.actualizarZona(id, dto);
        
        return new ResponseEntity<>(zona, HttpStatus.OK);
    }
    
    

    @Operation(summary = "Listar zonas por evento")
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<ZonaDTO>> listarZonasDelEvento(@PathVariable Long eventoId) {
        
        List<ZonaDTO> zonas = zonaService.listaZonasPorEvento(eventoId);

        return new ResponseEntity<>(zonas, HttpStatus.OK);
    }

    @Operation(summary = "Listar zonas sin staff por evento")
    @GetMapping("/evento/{eventoId}/sinstaff")
    public ResponseEntity<List<ZonaDTO>> listarZonasSinStaff(@PathVariable Long eventoId){
        
        List<ZonaDTO> zonas = zonaService.listaZonasSinStaff(eventoId);

        return new ResponseEntity<>(zonas, HttpStatus.OK);

    }
    @Operation(summary = "Eliminar una zona")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        zonaService.eliminarZona(id);
        return ResponseEntity.noContent().build();
    }


   

}
