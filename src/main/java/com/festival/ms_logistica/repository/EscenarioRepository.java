package com.festival.ms_logistica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.festival.ms_logistica.model.Escenario;

@Repository
public interface EscenarioRepository extends JpaRepository<Escenario, Long> {

    
} 
