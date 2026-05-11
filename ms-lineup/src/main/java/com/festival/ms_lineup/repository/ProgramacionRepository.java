package com.festival.ms_lineup.repository;

import com.festival.ms_lineup.model.ProgramacionArtista;
import com.festival.ms_lineup.model.EstadoProgramacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProgramacionRepository extends JpaRepository<ProgramacionArtista, Long> {

    // Obtener toda la programación de un evento
    List<ProgramacionArtista> encontrarIdEvento(Long eventoId);

    // Obtener programación de un artista específico
    List<ProgramacionArtista> encontrarIdArtista(Long artistaId);

    // Obtener programación por escenario y evento
    List<ProgramacionArtista> encontrarNombreEscenarioEIdEvento(
        String nombreEscenario, Long eventoId);

    // Verificar conflicto de horario en un escenario
    // Regla: no pueden haber dos artistas en el mismo escenario al mismo tiempo
    @Query("SELECT COUNT(p) > 0 FROM ProgramacionArtista p " +
        "WHERE p.nombreEscenario = :escenario " +
        "AND p.eventoId = :eventoId " +
        "AND p.estado != 'CANCELADO' " +
        "AND p.horaInicio < :horaFin " +
        "AND p.horaFin > :horaInicio")
    boolean existeConflictoHorario(
        @Param("escenario") String escenario,
        @Param("eventoId") Long eventoId,
        @Param("horaInicio") LocalDateTime horaInicio,
        @Param("horaFin") LocalDateTime horaFin
    );

    // Obtener programación por estado
    List<ProgramacionArtista> encontrarPorEstado(EstadoProgramacion estado);
}