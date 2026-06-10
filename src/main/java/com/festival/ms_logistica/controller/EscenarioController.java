package com.festival.ms_logistica.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.festival.ms_logistica.dto.EscenarioDTO;
import com.festival.ms_logistica.service.EscenarioService;

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




@Tag(name = "Escenarios", description = "Gestión de escenarios del festival")
@RestController
@RequestMapping("/api/escenarios")
@RequiredArgsConstructor
public class EscenarioController {
    
    private final EscenarioService escenarioService;
    
    @Operation(summary = "Crear un nuevo escenario")
    @PostMapping
    public ResponseEntity<EscenarioDTO> crearEscenario(@Valid @RequestBody EscenarioDTO dto) {
        
        EscenarioDTO escenario = escenarioService.crearEscenario(dto);
        
        return new ResponseEntity<>(escenario, HttpStatus.CREATED);
    }
    @Operation(summary = "Actualizar un escenario")
    @PutMapping("/{id}")
    public ResponseEntity<EscenarioDTO> actualizarEscenario(@PathVariable Long id, @Valid @RequestBody EscenarioDTO dto) {
        EscenarioDTO escenario = escenarioService.actualizEscenario(id, dto);
        
        return new ResponseEntity<>(escenario, HttpStatus.OK);
    }
    @Operation(summary = "Listar escenarios por evento")
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<EscenarioDTO>> ListarEscenariosDelEvento(@PathVariable Long eventoId){

        List<EscenarioDTO> escenarios = escenarioService.listaDeEscenariosPorEvento(eventoId);

        return new ResponseEntity<>(escenarios, HttpStatus.OK);
    }
    @Operation(summary = "Eliminar un escenario")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        escenarioService.eliminarEscenario(id);
        return ResponseEntity.noContent().build();

    }
    
    

}
