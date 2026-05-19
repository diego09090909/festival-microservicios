package com.festival.ms_usuario.client;

import com.festival.ms_usuario.dto.NotificacionDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class NotificacionClient {

    private static final Logger log = LoggerFactory.getLogger(NotificacionClient.class);
    private final RestTemplate restTemplate;

    public void enviarNotificacion(NotificacionDto notificacion) {
        try {
            restTemplate.postForObject(
                "http://localhost:8086/api/notificaciones",
                notificacion,
                Void.class
            );
            log.info("Notificacion enviada: tipo={}, usuarioId={}",
                notificacion.getTipo(), notificacion.getUsuarioId());
        } catch (Exception e) {
            log.warn("No se pudo enviar notificacion: {}", e.getMessage());
        }
    }
}