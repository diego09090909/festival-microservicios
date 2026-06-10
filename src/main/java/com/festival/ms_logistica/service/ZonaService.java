package com.festival.ms_logistica.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.festival.ms_logistica.exception.ResourceNotFoundException;
import com.festival.ms_logistica.mapper.ZonaMapper;
import com.festival.ms_logistica.model.AsignacionStaff;
import com.festival.ms_logistica.model.Zona;
import com.festival.ms_logistica.repository.AsignacionStaffRepository;
import com.festival.ms_logistica.repository.ZonaRepository;
import com.festival.ms_logistica.client.EventoClient;
import com.festival.ms_logistica.client.NotificacioneClient;
import com.festival.ms_logistica.dto.EventoDTO;
import com.festival.ms_logistica.dto.NotificacionDTO;
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
    private final EventoClient eventoClient;
    private final NotificacioneClient notificacioneClient;


   public ZonaDTO crearZona(ZonaDTO dto){
    log.info("Creando zona para el evento {}", dto.getEventoId());

    try {
        EventoDTO evento = eventoClient.obtenerEvento(dto.getEventoId());

        if (evento == null) {
            log.warn("El evento {} no existe", dto.getEventoId());
            throw new ResourceNotFoundException("El evento " + dto.getEventoId() + " no existe");
        }

        if (!evento.getEstado().equalsIgnoreCase("PUBLICADO")) {
            log.warn("El evento {} no esta publicado, estado actual: {}", dto.getEventoId(), evento.getEstado());
            throw new ResourceNotFoundException("El evento " + dto.getEventoId() + " no esta publicado");
        }

        Zona zona = zonaMapper.toEntity(dto);
        zona.setEventoNombre(evento.getNombre());
        Zona zonaGuardada = zonaRepository.save(zona);

        log.info("Zona creada con id {}", zonaGuardada.getId());

        return zonaMapper.toDTO(zonaGuardada);

    } catch (feign.FeignException.NotFound e) {
        log.error("Evento {} no encontrado en ms-evento", dto.getEventoId());
        throw new ResourceNotFoundException("El evento " + dto.getEventoId() + " no existe");

    } catch (feign.FeignException e) {
        log.error("Error al comunicarse con ms-evento: {}", e.getMessage());
        throw new RuntimeException("No se pudo validar el evento en este momento");
    }
}

    public ZonaDTO actualizarZona(Long id, ZonaDTO dto){
        log.info("Actualizando zona {}", id);

        Zona zona = zonaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la zona" + id));

        if (!zona.getEventoId().equals(dto.getEventoId())){
            EventoDTO evento = eventoClient.obtenerEvento(dto.getEventoId());


            if(!evento.getEstado().equalsIgnoreCase("PUBLICADO")){
                throw new ResourceNotFoundException("El evento" + dto.getEventoId() + " no esta publicado");
            }

            zona.setEventoId(dto.getEventoId());
            zona.setEventoNombre(evento.getNombre());
        }

        zona.setNombre(dto.getNombre());
        zona.setTipo(dto.getTipo());
        

        Zona zonaActualizada = zonaRepository.save(zona);

        log.info("Zona {} actualizado", id);
        

        try {
        notificacioneClient.crearNotificacion(new NotificacionDTO(
                "CAMBIO_ZONA",
                "Se realizo un cambio en la zona: " + zonaActualizada.getNombre(),
                dto.getEventoId()
            ));
        } catch (feign.FeignException e) {
            log.warn("Error al comunicarse con ms-notificaciones: {}", e.getMessage());
            throw new RuntimeException("No se pudo enviar la notificacion en este momento");
        }


        return zonaMapper.toDTO(zonaActualizada);
    }

    
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
        log.info("Listando las Zonas sin staff {}", eventoId);

        List<ZonaDTO> todasLasZonas = listaZonasPorEvento(eventoId);
        
        List<ZonaDTO> zonaSinStaff = todasLasZonas.stream()
            .filter(zona -> {
            
                List<AsignacionStaff> staff = asignacionStaffRepository.findByZonaId(zona.getId());
                return staff.isEmpty(); }).toList();
                
                if (zonaSinStaff.isEmpty()) {
                    log.info("Todas las zonas del evento {} tienen staff asignado", eventoId);
                    throw new ResourceNotFoundException("Todas las zonas del evento tienen staff asignado" + eventoId);
                }
                
            return zonaSinStaff;
    }


    public void eliminarZona(Long id){
        log.info("Eliminando zona {}", id);

        zonaRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("No se encontro las zona  {}", id);
                return new ResourceNotFoundException("No existe la zona" + id); });

        zonaRepository.deleteById(id);

    }


}

       



