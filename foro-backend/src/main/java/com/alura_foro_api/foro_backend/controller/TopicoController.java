package com.alura_foro_api.foro_backend.controller;

import com.alura_foro_api.foro_backend.dto.DetalleTopicoDTO;
import com.alura_foro_api.foro_backend.dto.RegistroTopicoDTO;
import com.alura_foro_api.foro_backend.model.Topico;
import com.alura_foro_api.foro_backend.repository.TopicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Optional;


@RestController
@RequestMapping("/topicos")
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepository;

    @PostMapping
    public ResponseEntity<?> registrarTopico(@RequestBody @Valid RegistroTopicoDTO datos) {
        if (topicoRepository.findByTituloAndMensaje(datos.getTitulo(), datos.getMensaje()).isPresent()) {
            return ResponseEntity.badRequest().body("Tópico duplicado");
        }
        Topico topico = new Topico();
        topico.setTitulo(datos.getTitulo());
        topico.setMensaje(datos.getMensaje());
        topico.setAutor(datos.getAutor());
        topico.setCurso(datos.getCurso());
        topicoRepository.save(topico);
        return ResponseEntity.ok(topico);
    }

    @GetMapping
    public Page<DetalleTopicoDTO> listar(
            @RequestParam(required = false) String curso,
            @RequestParam(required = false) Integer anio,
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        if (curso != null && anio != null) {
            LocalDateTime start = Year.of(anio).atDay(1).atStartOfDay();
            LocalDateTime end = Year.of(anio).atMonth(12).atEndOfMonth().atTime(23,59,59);
            return topicoRepository.findByCursoAndFechaCreacionBetween(curso, start, end, pageable)
                    .map(DetalleTopicoDTO::new);
        }
        return topicoRepository.findAll(pageable).map(DetalleTopicoDTO::new);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleTopicoDTO> detalle(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Topico> topico = topicoRepository.findById(id);
        if (topico.isPresent()) {
            return ResponseEntity.ok(new DetalleTopicoDTO(topico.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTopico(
            @PathVariable Long id,
            @RequestBody @Valid RegistroTopicoDTO datos) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body("ID inválido");
        }
        Optional<Topico> optionalTopico = topicoRepository.findById(id);
        if (optionalTopico.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // Verifica duplicados (excluyendo el propio tópico)
        Optional<Topico> duplicado = topicoRepository.findByTituloAndMensaje(datos.getTitulo(), datos.getMensaje());
        if (duplicado.isPresent() && !duplicado.get().getId().equals(id)) {
            return ResponseEntity.badRequest().body("Tópico duplicado");
        }
        Topico topico = optionalTopico.get();
        topico.setTitulo(datos.getTitulo());
        topico.setMensaje(datos.getMensaje());
        topico.setAutor(datos.getAutor());
        topico.setCurso(datos.getCurso());
        topicoRepository.save(topico);
        return ResponseEntity.ok(new DetalleTopicoDTO(topico));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTopico(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body("ID inválido");
        }
        Optional<Topico> topico = topicoRepository.findById(id);
        if (topico.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        topicoRepository.deleteById(id);
        return ResponseEntity.ok("Tópico eliminado correctamente");
    }
}
