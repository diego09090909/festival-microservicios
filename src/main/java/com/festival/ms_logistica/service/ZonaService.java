package com.festival.ms_logistica.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.festival.ms_logistica.exception.ResourceNotFoundException;
import com.festival.ms_logistica.mapper.ZonaMapper;
import com.festival.ms_logistica.model.AsignacionStaff;
import com.festival.ms_logistica.model.Zona;
import com.festival.ms_logistica.repository.AsignacionStaffRepository;
import com.festival.ms_logistica.repository.ZonaRepository;
import com.festival.ms_logistica.dto.ZonaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZonaService {
    
    private final ZonaRepository zonaRepository;
    private final AsignacionStaffRepository asignacionStaffRepository;

    private final ZonaMapper zonaMapper;

    public List<ZonaDTO> listaZonasPorEvento(Long eventoId){
        log.info("Listando las Zonas del evento {}", eventoId);

        List<Zona> zonas = zonaRepository.findByEventoId(eventoId);
        
        if (zonas.isEmpty()) {
            log.warn("No se encontraron las zonas del evento {}", eventoId);
             throw new ResourceNotFoundException("No existen zonas para el evento" + eventoId);
        }
        return zonas.stream()
                .map(z -> zonaMapper.toDTO(z))
                .toList();
                
    }


    
    

    public List<ZonaDTO> listaZonasSinStaff(Long eventoId){
        log.info("Listando las Zonas sin staff {}", eventoId)

        List<ZonaDTO> todasLasZonas = listaZonasPorEvento(eventoId);
        
        List<ZonaDTO> zonaSinStaff = todasLasZonas.stream()
            .filter(zona -> {
            
                List<AsignacionStaff> staff = asignacionStaffRepository.findByZonaId(zona.getEventoId());
                return staff.isEmpty();

            })
            .toList();
        
    }


}
        



