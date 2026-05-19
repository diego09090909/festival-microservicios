package com.festival.ms_lineup.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.festival.ms_lineup.dto.EventoDTORespuesta;

@FeignClient(name = "ms-eventos", url = "http://localhost:8802")
public interface EventoClient {

    @GetMapping("/api/eventos/{id}")
    EventoDTORespuesta obtenerEvento(@PathVariable Long id);
}