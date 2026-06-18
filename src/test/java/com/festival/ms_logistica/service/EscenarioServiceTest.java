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
import com.festival.ms_logistica.dto.EscenarioDTO;
import com.festival.ms_logistica.dto.EventoDTO;
import com.festival.ms_logistica.exception.ResourceNotFoundException;
import com.festival.ms_logistica.mapper.EscenarioMapper;
import com.festival.ms_logistica.model.Escenario;
import com.festival.ms_logistica.repository.EscenarioRepository;
import com.festival.ms_logistica.util.EscenarioTestDataFactory;
import com.festival.ms_logistica.util.ZonaTestDataFactory;

@ExtendWith(MockitoExtension.class)
class EscenarioServiceTest {

    @Mock
    private EscenarioRepository escenarioRepository;

    @Mock
    private EscenarioMapper escenarioMapper;

    @Mock
    private EventoClient eventoClient;

    @Mock
    private NotificacioneClient notificacioneClient;

    @InjectMocks
    private EscenarioService escenarioService;

    private EscenarioDTO dto;
    private Escenario escenarioEntidad;
    private EventoDTO eventoPublicado;

    @BeforeEach
    void setUp() {
        dto = EscenarioTestDataFactory.escenarioDtoFalso();
        escenarioEntidad = EscenarioTestDataFactory.escenarioEntidadFalso();
        eventoPublicado = ZonaTestDataFactory.eventoPublicadoFalso(); // reutilizamos el mismo helper de evento
    }

    @Test
    @DisplayName("crearEscenario debe guardar el escenario cuando el evento existe y esta PUBLICADO")
    void crearEscenarioCuandoEventoValidoDebeGuardar() {

        when(eventoClient.obtenerEvento(1L)).thenReturn(eventoPublicado);
        when(escenarioMapper.toEntity(dto)).thenReturn(escenarioEntidad);
        when(escenarioRepository.save(any(Escenario.class))).thenReturn(escenarioEntidad);

        EscenarioDTO esperado = new EscenarioDTO();
        esperado.setId(1L);
        esperado.setNombre("Escenario Principal");
        when(escenarioMapper.toDTO(escenarioEntidad)).thenReturn(esperado);

        EscenarioDTO resultado = escenarioService.crearEscenario(dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Escenario Principal", resultado.getNombre());

        verify(escenarioRepository, times(1)).save(any(Escenario.class));
    }

    @Test
    @DisplayName("crearEscenario debe lanzar ResourceNotFoundException cuando el evento no existe")
    void crearEscenarioCuandoEventoNoExisteDebeLanzarExcepcion() {

        when(eventoClient.obtenerEvento(1L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> escenarioService.crearEscenario(dto));

        verify(escenarioRepository, never()).save(any(Escenario.class));
    }

    @Test
    @DisplayName("crearEscenario debe lanzar ResourceNotFoundException cuando el evento no esta PUBLICADO")
    void crearEscenarioCuandoEventoNoPublicadoDebeLanzarExcepcion() {

        EventoDTO eventoNoPublicado = ZonaTestDataFactory.eventoNoPublicadoFalso();

        when(eventoClient.obtenerEvento(1L)).thenReturn(eventoNoPublicado);

        assertThrows(ResourceNotFoundException.class,
                () -> escenarioService.crearEscenario(dto));

        verify(escenarioRepository, never()).save(any(Escenario.class));
    }

    @Test
    @DisplayName("actualizEscenario debe actualizar nombre y capacidad cuando el escenario existe")
    void actualizarEscenarioCuandoExisteDebeActualizar() {

        EscenarioDTO dtoActualizado = EscenarioTestDataFactory.escenarioDtoFalso();
        dtoActualizado.setNombre("Escenario Principal Renovado");
        dtoActualizado.setCapacidad(3000);

        when(escenarioRepository.findById(1L)).thenReturn(Optional.of(escenarioEntidad));
        when(escenarioRepository.save(any(Escenario.class))).thenReturn(escenarioEntidad);

        EscenarioDTO esperado = new EscenarioDTO();
        esperado.setId(1L);
        esperado.setNombre("Escenario Principal Renovado");
        esperado.setCapacidad(3000);
        when(escenarioMapper.toDTO(escenarioEntidad)).thenReturn(esperado);

        EscenarioDTO resultado = escenarioService.actualizEscenario(1L, dtoActualizado);

        assertEquals("Escenario Principal Renovado", resultado.getNombre());
        assertEquals(3000, resultado.getCapacidad());

        verify(eventoClient, never()).obtenerEvento(any());
        verify(notificacioneClient, times(1)).crearNotificacion(any());
    }

    @Test
    @DisplayName("actualizEscenario debe validar el nuevo evento cuando el eventoId cambia")
    void actualizarEscenarioCuandoCambiaEventoDebeValidar() {

        EscenarioDTO dtoConOtroEvento = EscenarioTestDataFactory.escenarioDtoFalso();
        dtoConOtroEvento.setEventoId(2L);

        when(escenarioRepository.findById(1L)).thenReturn(Optional.of(escenarioEntidad));
        when(eventoClient.obtenerEvento(2L)).thenReturn(eventoPublicado);
        when(escenarioRepository.save(any(Escenario.class))).thenReturn(escenarioEntidad);
        when(escenarioMapper.toDTO(escenarioEntidad)).thenReturn(new EscenarioDTO());

        escenarioService.actualizEscenario(1L, dtoConOtroEvento);

        verify(eventoClient, times(1)).obtenerEvento(2L);
    }

    @Test
    @DisplayName("actualizEscenario debe lanzar ResourceNotFoundException cuando el escenario no existe")
    void actualizarEscenarioCuandoNoExisteDebeLanzarExcepcion() {

        when(escenarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> escenarioService.actualizEscenario(99L, dto));

        verify(escenarioRepository, never()).save(any(Escenario.class));
    }

    @Test
    @DisplayName("listaDeEscenariosPorEvento debe retornar la lista cuando existen escenarios")
    void listaEscenariosPorEventoConDatosDebeRetornarLista() {

        when(escenarioRepository.findByEventoId(1L)).thenReturn(List.of(escenarioEntidad));

        EscenarioDTO dtoEsperado = new EscenarioDTO();
        dtoEsperado.setId(1L);
        when(escenarioMapper.toDTO(escenarioEntidad)).thenReturn(dtoEsperado);

        List<EscenarioDTO> resultado = escenarioService.listaDeEscenariosPorEvento(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
    }

    @Test
    @DisplayName("listaDeEscenariosPorEvento debe lanzar excepcion cuando no hay escenarios")
    void listaEscenariosPorEventoSinDatosDebeLanzarExcepcion() {

        when(escenarioRepository.findByEventoId(99L)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> escenarioService.listaDeEscenariosPorEvento(99L));
    }

    @Test
    @DisplayName("eliminarEscenario debe eliminar cuando el escenario existe")
    void eliminarEscenarioCuandoExisteDebeEliminar() {

        when(escenarioRepository.findById(1L)).thenReturn(Optional.of(escenarioEntidad));

        escenarioService.eliminarEscenario(1L);

        verify(escenarioRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminarEscenario debe lanzar excepcion cuando el escenario no existe")
    void eliminarEscenarioCuandoNoExisteDebeLanzarExcepcion() {

        when(escenarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> escenarioService.eliminarEscenario(99L));

        verify(escenarioRepository, never()).deleteById(any());
    }
}
