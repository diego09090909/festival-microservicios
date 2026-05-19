package com.festival.ms_usuario.controller;

import com.festival.ms_usuario.dto.LoginDTO;
import com.festival.ms_usuario.dto.TokenResponseDTO;
import com.festival.ms_usuario.dto.UsuarioDto;
import com.festival.ms_usuario.model.Rol;
import com.festival.ms_usuario.model.Usuario;
import com.festival.ms_usuario.repository.RolRepository;
import com.festival.ms_usuario.repository.UsuarioRepository;
import com.festival.ms_usuario.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;


    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        log.info("Intento de login: {}", dto.getEmail());

        Authentication auth = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

            Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            String token = jwtUtil.generarToken(
            usuario.getEmail(),
            usuario.getRol().getNombre()
        );

            log.info("Login exitoso para: {}", dto.getEmail());
            return ResponseEntity.ok(
            new TokenResponseDTO(token, usuario.getEmail(), usuario.getRol().getNombre())
        );
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioDto> registro(@Valid @RequestBody UsuarioDto dto) {
        log.info("Registro de nuevo usuario: {}", dto.getEmail());

    // existsByEmail devuelve boolean directamente
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya esta registrado");
        }

            Rol rol = rolRepository.findById(dto.getRolId())
            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(rol);
        usuario.setActivo(true);

        Usuario guardado = usuarioRepository.save(usuario);

        UsuarioDto respuesta = new UsuarioDto();
        respuesta.setId(guardado.getId());
        respuesta.setNombre(guardado.getNombre());
        respuesta.setEmail(guardado.getEmail());
        respuesta.setRolId(guardado.getRol().getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
}