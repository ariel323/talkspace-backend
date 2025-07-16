package com.alura_foro_api.foro_backend.repository;

import com.alura_foro_api.foro_backend.model.Topico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TopicoRepository extends JpaRepository<Topico, Long> {
    Optional<Topico> findByTituloAndMensaje(String titulo, String mensaje);
    Optional<Topico> findByTituloAndAutor(String titulo, String autor);

    Page<Topico> findByCursoAndFechaCreacionBetween(String curso, LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    // Nuevos métodos de búsqueda y filtros
    Page<Topico> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);
    Page<Topico> findByCursoContainingIgnoreCase(String curso, Pageable pageable);
    Page<Topico> findByAutorContainingIgnoreCase(String autor, Pageable pageable);
    Page<Topico> findByCurso(String curso, Pageable pageable);
    Page<Topico> findByAutor(String autor, Pageable pageable);
    
    // Búsqueda combinada con Query personalizada
    @Query("SELECT t FROM Topico t WHERE " +
           "(:titulo IS NULL OR LOWER(t.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))) AND " +
           "(:curso IS NULL OR LOWER(t.curso) LIKE LOWER(CONCAT('%', :curso, '%'))) AND " +
           "(:autor IS NULL OR LOWER(t.autor) LIKE LOWER(CONCAT('%', :autor, '%'))) AND " +
           "(:fechaInicio IS NULL OR t.fechaCreacion >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR t.fechaCreacion <= :fechaFin)")
    Page<Topico> buscarConFiltros(
            @Param("titulo") String titulo,
            @Param("curso") String curso,
            @Param("autor") String autor,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable);
}