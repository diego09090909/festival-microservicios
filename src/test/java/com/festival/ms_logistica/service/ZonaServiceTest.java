package com.festival.ms_logistica.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.festival.ms_logistica.client.EventoClient;
import com.festival.ms_logistica.client.NotificacioneClient;
import com.festival.ms_logistica.dto.EventoDTO;
import com.festival.ms_logistica.dto.ZonaDTO;
import com.festival.ms_logistica.exception.ResourceNotFoundException;
import com.festival.ms_logistica.mapper.ZonaMapper;
import com.festival.ms_logistica.model.AsignacionStaff;
import com.festival.ms_logistica.model.Zona;
import com.festival.ms_logistica.repository.AsignacionStaffRepository;
import com.festival.ms_logistica.repository.ZonaRepository;
import com.festival.ms_logistica.util.ZonaTestDataFactory;

@ExtendWith(MockitoExtension.class)
class ZonaServiceTest {

    @Mock
    private ZonaRepository zonaRepository;

    @Mock
    private AsignacionStaffRepository asignacionStaffRepository;

    @Mock
    private ZonaMapper zonaMapper;

    @Mock
    private EventoClient eventoClient;

    @Mock
    private NotificacioneClient notificacioneClient;

    @InjectMocks
    private ZonaService zonaService;

    private ZonaDTO dto;
    private Zona zonaEntidad;
    private EventoDTO eventoPublicado;

    @BeforeEach
    void setUp() {
        dto = ZonaTestDataFactory.zonaDtoFalsa();
        zonaEntidad = ZonaTestDataFactory.zonaEntidadFalsa();
        eventoPublicado = ZonaTestDataFactory.eventoPublicadoFalso();
    }


    @Test
    @DisplayName("listaZonasPorEvento debe retornar la lista cuando existen zonas")
    void listaZonasPorEventoConDatosDebeRetornarLista() {

        when(zonaRepository.findByEventoId(1L)).thenReturn(List.of(zonaEntidad));

        ZonaDTO dtoEsperado = new ZonaDTO();
        dtoEsperado.setId(1L);
        when(zonaMapper.toDTO(zonaEntidad)).thenReturn(dtoEsperado);

        List<ZonaDTO> resultado = zonaService.listaZonasPorEvento(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
    }

    @Test
    @DisplayName("listaZonasPorEvento debe lanzar excepcion cuando no hay zonas")
    void listaZonasPorEventoSinDatosDebeLanzarExcepcion() {

        when(zonaRepository.findByEventoId(99L)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> zonaService.listaZonasPorEvento(99L));
    }

    @Test
    @DisplayName("listaZonasSinStaff debe retornar solo las zonas sin staff asignado")
    void listaZonasSinStaffDebeFiltrarCorrectamente() {

        when(zonaRepository.findByEventoId(1L)).thenReturn(List.of(zonaEntidad));

        ZonaDTO zonaDtoSinStaff = new ZonaDTO();
        zonaDtoSinStaff.setId(1L);
        when(zonaMapper.toDTO(zonaEntidad)).thenReturn(zonaDtoSinStaff);

        when(asignacionStaffRepository.findByZonaId(1L)).thenReturn(List.of());

        List<ZonaDTO> resultado = zonaService.listaZonasSinStaff(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
    }

    @Test
    @DisplayName("listaZonasSinStaff debe lanzar excepcion cuando todas las zonas tienen staff")
    void listaZonasSinStaffCuandoTodasTienenStaffDebeLanzarExcepcion() {

        when(zonaRepository.findByEventoId(1L)).thenReturn(List.of(zonaEntidad));

        ZonaDTO zonaDtoConStaff = new ZonaDTO();
        zonaDtoConStaff.setId(1L);
        when(zonaMapper.toDTO(zonaEntidad)).thenReturn(zonaDtoConStaff);

        AsignacionStaff staff = new AsignacionStaff();
        when(asignacionStaffRepository.findByZonaId(1L)).thenReturn(List.of(staff));

        assertThrows(ResourceNotFoundException.class,
                () -> zonaService.listaZonasSinStaff(1L));
    }

    @Test
    @DisplayName("eliminarZona debe eliminar cuando la zona existe")
    void eliminarZonaCuandoExisteDebeEliminar() {

        when(zonaRepository.findById(1L)).thenReturn(Optional.of(zonaEntidad));

        zonaService.eliminarZona(1L);

        verify(zonaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminarZona debe lanzar excepcion cuando la zona no existe")
    void eliminarZonaCuandoNoExisteDebeLanzarExcepcion() {

        when(zonaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> zonaService.eliminarZona(99L));

        verify(zonaRepository, never()).deleteById(any());
    }
}