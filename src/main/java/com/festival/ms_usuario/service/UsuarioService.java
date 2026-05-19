package com.festival.ms_usuario.service;

import com.festival.ms_usuario.dto.UsuarioDto;
import com.festival.ms_usuario.mapper.UsuarioMapper;
import com.festival.ms_usuario.model.Rol;
import com.festival.ms_usuario.model.Usuario;
import com.festival.ms_usuario.repository.RolRepository;
import com.festival.ms_usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioMapper usuarioMapper; // 

    public List<UsuarioDto> listarActivos() {
        log.info("Listando todos los usuarios activos");
        return usuarioRepository.findByActivoTrue()
                .stream()
                .map(usuarioMapper::toDTO) // 
                .collect(Collectors.toList());
    }

    public UsuarioDto buscarPorId(Long id) {
        log.info("Buscando usuario con ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado con ID: {}", id);
                    return new RuntimeException("Usuario no encontrado con ID: " + id);
                });
        return usuarioMapper.toDTO(usuario); // 
    }

    public UsuarioDto crear(UsuarioDto dto) {
        log.info("Creando usuario con email: {}", dto.getEmail());

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            log.warn("Email ya registrado: {}", dto.getEmail());
            throw new RuntimeException("Ya existe un usuario con el email: " + dto.getEmail());
        }

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + dto.getRolId()));

        // ← CAMBIADO: el mapper construye la entidad base
        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setPassword(dto.getPassword()); 
        usuario.setRol(rol);

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario creado exitosamente con ID: {}", guardado.getId());
        return usuarioMapper.toDTO(guardado); // 
    }

    
    public UsuarioDto actualizar(Long id, UsuarioDto dto) {
        log.info("Actualizando usuario con ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + dto.getRolId()));

        
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setRol(rol);

        Usuario actualizado = usuarioRepository.save(usuario);
        log.info("Usuario actualizado: {}", actualizado.getId());
        return usuarioMapper.toDTO(actualizado); // ← CAMBIADO
    }

    public void desactivar(Long id) {
        log.info("Desactivando usuario con ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        log.info("Usuario desactivado: {}", id);
    }

}