package com.festival.ms_lineup.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.festival.ms_lineup.dto.NotificacionPedidoDTO;

@FeignClient(name = "ms-notificaciones", url = "http://localhost:8806")
public interface NotificacionClient {

    @PostMapping("/api/v1/notificaciones")
    void enviarNotificacion(@RequestBody NotificacionPedidoDTO dto);
}