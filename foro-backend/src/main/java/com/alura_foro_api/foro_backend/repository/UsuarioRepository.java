package com.alura_foro_api.foro_backend.repository;

import com.alura_foro_api.foro_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByUsername(String username);
    Optional<Usuario> findByUsername(String username);
}
