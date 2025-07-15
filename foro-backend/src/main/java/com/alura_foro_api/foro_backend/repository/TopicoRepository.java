package com.alura_foro_api.foro_backend.repository;

import com.alura_foro_api.foro_backend.model.Topico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TopicoRepository extends JpaRepository<Topico, Long> {
    Optional<Topico> findByTituloAndMensaje(String titulo, String mensaje);
    Optional<Topico> findByTituloAndAutor(String titulo, String autor);

    Page<Topico> findByCursoAndFechaCreacionBetween(String curso, LocalDateTime start, LocalDateTime end, Pageable pageable);
}