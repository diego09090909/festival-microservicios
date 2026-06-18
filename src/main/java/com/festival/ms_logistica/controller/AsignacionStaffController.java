package com.festival.ms_logistica.controller;

import java.util.List;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.festival.ms_logistica.dto.AsignacionStaffDTO;
import com.festival.ms_logistica.service.AsignacionStaffService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;




@Tag(name = "Asignacion Staff", description = "Gestión de asignación de staff a zonas del festival")
@RestController
@RequestMapping("/api/asignaciones")
@RequiredArgsConstructor
public class AsignacionStaffController {
    
private final AsignacionStaffService asignacionStaffService;

    @Operation(summary = "Asignar staff a una zona")
    @PostMapping("/{usuarioId}")
    public ResponseEntity<AsignacionStaffDTO> asignacion (@PathVariable Long usuarioId, @Valid @RequestBody  AsignacionStaffDTO dto) {
        
        AsignacionStaffDTO asignacion = asignacionStaffService.asignarStaff(usuarioId, dto);
        
        return new ResponseEntity<>(asignacion, HttpStatus.CREATED);
    }
    
    @Operation(summary = "Actualizar asignación de staff")
    @PutMapping("/{usuarioId}")
    public ResponseEntity<AsignacionStaffDTO> actualizarStaff(@PathVariable Long usuarioId, @Valid @RequestBody AsignacionStaffDTO dto) {

            AsignacionStaffDTO asignacionActualizada = 
            asignacionStaffService.actualizarAsignacion(usuarioId, dto);

            return new ResponseEntity<>(asignacionActualizada, HttpStatus.OK);
    }


    @Operation(summary = "Listar staff por zona")
    @GetMapping("/staffZona/{zonaId}")
    public ResponseEntity<List<AsignacionStaffDTO>> listarStaffPorZona(@PathVariable long zonaId){

        List<AsignacionStaffDTO> asignacion = asignacionStaffService.ListaStaffPorZona(zonaId);

        return new ResponseEntity<>(asignacion, HttpStatus.OK); 
    }
    @Operation(summary = "Listar staff por evento")
    @GetMapping("/staffEvento/{eventoId}")
    public ResponseEntity<List<AsignacionStaffDTO>> listarStaffPorEvento(@PathVariable long eventoId){

        List<AsignacionStaffDTO> asignacion2 = asignacionStaffService.listaStaffPorEvento(eventoId);

        return new ResponseEntity<>(asignacion2, HttpStatus.OK); 
    }

    @Operation(summary = "Eliminar asignación de staff")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAsignacion(@PathVariable Long id){
        asignacionStaffService.eliminarAsignacion(id);
        return ResponseEntity.noContent().build();
    }


}
