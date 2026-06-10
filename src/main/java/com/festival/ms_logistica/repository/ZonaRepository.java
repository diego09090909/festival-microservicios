package com.festival.ms_logistica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.festival.ms_logistica.model.Zona;
@Repository
public interface ZonaRepository extends JpaRepository<Zona, Long>{

    List<Zona> findByEventoId(Long eventoId);
    
} 
