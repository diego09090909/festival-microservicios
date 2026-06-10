package com.festival.ms_logistica.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.festival.ms_logistica.dto.UsuarioDTO;

@FeignClient(name = "ms-usuarios", url = "http://localhost:8801")
public interface UsuarioClient {
    
    @GetMapping("/api/usuarios/{id}")
    UsuarioDTO obtenerUsuario(@PathVariable Long id);

}
