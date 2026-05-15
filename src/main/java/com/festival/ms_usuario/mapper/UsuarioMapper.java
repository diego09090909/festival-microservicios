package com.festival.ms_usuario.mapper;

import com.festival.ms_usuario.DTO.UsuarioDto;
import com.festival.ms_usuario.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioDto toDTO(Usuario usuario) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setRolId(usuario.getRol().getId());
        dto.setRolNombre(usuario.getRol().getNombre());
        dto.setActivo(usuario.getActivo());
        // NUNCA se mapea el password hacia el DTO
        return dto;
    }

    public Usuario toEntity(UsuarioDto dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setActivo(true);
        // password se encripta en el Service con BCrypt, no aquí
        return usuario;
    }
}