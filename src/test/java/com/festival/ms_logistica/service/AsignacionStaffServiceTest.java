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

import com.festival.ms_logistica.client.NotificacioneClient;
import com.festival.ms_logistica.client.UsuarioClient;
import com.festival.ms_logistica.dto.AsignacionStaffDTO;
import com.festival.ms_logistica.dto.UsuarioDTO;
import com.festival.ms_logistica.exception.ResourceNotFoundException;
import com.festival.ms_logistica.mapper.AsignacionStaffMapper;
import com.festival.ms_logistica.model.AsignacionStaff;
import com.festival.ms_logistica.model.Zona;
import com.festival.ms_logistica.repository.AsignacionStaffRepository;
import com.festival.ms_logistica.repository.ZonaRepository;
import com.festival.ms_logistica.util.AsignacionStaffTestDataFactory;
import com.festival.ms_logistica.util.ZonaTestDataFactory;

@ExtendWith(MockitoExtension.class)
class AsignacionStaffServiceTest {

    @Mock
    private AsignacionStaffMapper asignacionStaffMapper;

    @Mock
    private AsignacionStaffRepository asignacionStaffRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private ZonaRepository zonaRepository;

    @Mock
    private NotificacioneClient notificacioneClient;

    @InjectMocks
    private AsignacionStaffService asignacionStaffService;

    private AsignacionStaffDTO dto;
    private AsignacionStaff asignacionEntidad;
    private UsuarioDTO usuarioStaff;
    private Zona zona;

    @BeforeEach
    void setUp() {
        dto = AsignacionStaffTestDataFactory.asignacionDtoFalsa();
        zona = ZonaTestDataFactory.zonaEntidadFalsa();
        usuarioStaff = AsignacionStaffTestDataFactory.usuarioStaffFalso();
        asignacionEntidad = AsignacionStaffTestDataFactory.asignacionEntidadFalsa(zona);
    }

    @Test
    @DisplayName("asignarStaff debe guardar la asignacion cuando el usuario es STAFF y la zona existe")
    void asignarStaffCuandoUsuarioValidoDebeGuardar() {

        when(usuarioClient.obtenerUsuario(1L)).thenReturn(usuarioStaff);
        when(zonaRepository.findById(1L)).thenReturn(Optional.of(zona));
        when(asignacionStaffRepository.existsByUsuarioIdAndZona_Id(1L, 1L)).thenReturn(false);
        when(asignacionStaffRepository.save(any(AsignacionStaff.class))).thenReturn(asignacionEntidad);

        AsignacionStaffDTO esperado = new AsignacionStaffDTO();
        esperado.setId(1L);
        esperado.setLabor("Seguridad");
        when(asignacionStaffMapper.toDTO(asignacionEntidad)).thenReturn(esperado);

        AsignacionStaffDTO resultado = asignacionStaffService.asignarStaff(1L, dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Seguridad", resultado.getLabor());

        verify(asignacionStaffRepository, times(1)).save(any(AsignacionStaff.class));
    }

    @Test
    @DisplayName("asignarStaff debe lanzar ResourceNotFoundException cuando el usuario no existe")
    void asignarStaffCuandoUsuarioNoExisteDebeLanzarExcepcion() {

        when(usuarioClient.obtenerUsuario(1L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> asignacionStaffService.asignarStaff(1L, dto));

        verify(asignacionStaffRepository, never()).save(any(AsignacionStaff.class));
    }

    @Test
    @DisplayName("asignarStaff debe lanzar ResourceNotFoundException cuando el usuario no tiene rol STAFF")
    void asignarStaffCuandoUsuarioNoEsStaffDebeLanzarExcepcion() {

        UsuarioDTO usuarioNoStaff = AsignacionStaffTestDataFactory.usuarioNoStaffFalso();

        when(usuarioClient.obtenerUsuario(1L)).thenReturn(usuarioNoStaff);

        assertThrows(ResourceNotFoundException.class,
                () -> asignacionStaffService.asignarStaff(1L, dto));

        verify(asignacionStaffRepository, never()).save(any(AsignacionStaff.class));
    }

    @Test
    @DisplayName("asignarStaff debe lanzar ResourceNotFoundException cuando la zona no existe")
    void asignarStaffCuandoZonaNoExisteDebeLanzarExcepcion() {

        when(usuarioClient.obtenerUsuario(1L)).thenReturn(usuarioStaff);
        when(zonaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> asignacionStaffService.asignarStaff(1L, dto));

        verify(asignacionStaffRepository, never()).save(any(AsignacionStaff.class));
    }

    @Test
    @DisplayName("asignarStaff debe lanzar IllegalStateException cuando el staff ya esta asignado a la zona")
    void asignarStaffCuandoYaAsignadoDebeLanzarExcepcion() {

        when(usuarioClient.obtenerUsuario(1L)).thenReturn(usuarioStaff);
        when(zonaRepository.findById(1L)).thenReturn(Optional.of(zona));
        when(asignacionStaffRepository.existsByUsuarioIdAndZona_Id(1L, 1L)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> asignacionStaffService.asignarStaff(1L, dto));

        verify(asignacionStaffRepository, never()).save(any(AsignacionStaff.class));
    }

    @Test
    @DisplayName("actualizarAsignacion debe actualizar labor y zona cuando los datos son validos")
    void actualizarAsignacionCuandoValidaDebeActualizar() {

        AsignacionStaffDTO dtoActualizado = AsignacionStaffTestDataFactory.asignacionDtoFalsa();
        dtoActualizado.setLabor("Logistica");

        when(asignacionStaffRepository.findById(1L)).thenReturn(Optional.of(asignacionEntidad));
        when(zonaRepository.findById(1L)).thenReturn(Optional.of(zona));
        when(asignacionStaffRepository.save(any(AsignacionStaff.class))).thenReturn(asignacionEntidad);

        AsignacionStaffDTO esperado = new AsignacionStaffDTO();
        esperado.setLabor("Logistica");
        when(asignacionStaffMapper.toDTO(asignacionEntidad)).thenReturn(esperado);

        AsignacionStaffDTO resultado = asignacionStaffService.actualizarAsignacion(1L, dtoActualizado);

        assertEquals("Logistica", resultado.getLabor());

        verify(notificacioneClient, times(1)).crearNotificacion(any());
    }

    @Test
    @DisplayName("actualizarAsignacion debe lanzar ResourceNotFoundException cuando la asignacion no existe")
    void actualizarAsignacionCuandoNoExisteDebeLanzarExcepcion() {

        when(asignacionStaffRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> asignacionStaffService.actualizarAsignacion(99L, dto));

        verify(asignacionStaffRepository, never()).save(any(AsignacionStaff.class));
    }

    @Test
    @DisplayName("actualizarAsignacion debe lanzar ResourceNotFoundException cuando se intenta cambiar el usuario")
    void actualizarAsignacionCuandoCambiaUsuarioDebeLanzarExcepcion() {

        AsignacionStaffDTO dtoOtroUsuario = AsignacionStaffTestDataFactory.asignacionDtoFalsa();
        dtoOtroUsuario.setUsuarioId(2L);

        when(asignacionStaffRepository.findById(1L)).thenReturn(Optional.of(asignacionEntidad));

        assertThrows(ResourceNotFoundException.class,
                () -> asignacionStaffService.actualizarAsignacion(1L, dtoOtroUsuario));

        verify(asignacionStaffRepository, never()).save(any(AsignacionStaff.class));
    }
}