package com.festival.ms_logistica.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;

import com.festival.ms_logistica.dto.NotificacionDTO;

@FeignClient(name = "ms-notificaciones", url = "http://localhost:8806")
public interface NotificacioneClient {
    
    @PostMapping("/api/notificaciones")
    NotificacionDTO crearNotificacion(@RequestBody NotificacionDTO dto);
}
