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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/topicos")
@Tag(name = "Tópicos", description = "API para gestión de tópicos del foro")
public class TopicoController {

    private static final Logger logger = LoggerFactory.getLogger(TopicoController.class);

    @Autowired
    private TopicoRepository topicoRepository;

    @PostMapping
    @Operation(summary = "Crear un nuevo tópico")
    @ApiResponse(responseCode = "200", description = "Tópico creado exitosamente")
    public ResponseEntity<?> registrarTopico(@RequestBody @Valid RegistroTopicoDTO datos) {
        logger.info("Creando nuevo tópico con título: {}", datos.getTitulo());
        
        // Validar duplicados
        if (topicoRepository.findByTituloAndMensaje(datos.getTitulo(), datos.getMensaje()).isPresent()) {
            return ResponseEntity.badRequest().body("Tópico duplicado");
        }
        
        Topico topico = new Topico();
        topico.setTitulo(datos.getTitulo());
        topico.setMensaje(datos.getMensaje());
        topico.setAutor(datos.getAutor());
        topico.setCurso(datos.getCurso());
        
        Topico topicoGuardado = topicoRepository.save(topico);
        logger.info("Tópico creado exitosamente con ID: {}", topicoGuardado.getId());
        
        return ResponseEntity.ok(new DetalleTopicoDTO(topicoGuardado));
    }

    @GetMapping
    @Operation(summary = "Listar tópicos")
    public ResponseEntity<Page<DetalleTopicoDTO>> listarTopicos(
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable) {
        logger.info("Listando tópicos - página: {}, tamaño: {}", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Topico> topicos = topicoRepository.findAll(pageable);
        Page<DetalleTopicoDTO> detalleTopicos = topicos.map(DetalleTopicoDTO::new);
        
        return ResponseEntity.ok(detalleTopicos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tópico por ID")
    public ResponseEntity<DetalleTopicoDTO> detalle(@PathVariable Long id) {
        logger.info("Consultando tópico con ID: {}", id);
        
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
    @Operation(summary = "Actualizar un tópico")
    public ResponseEntity<?> actualizarTopico(
            @PathVariable Long id,
            @RequestBody @Valid RegistroTopicoDTO datos) {
        
        logger.info("Actualizando tópico con ID: {}", id);
        
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body("ID inválido");
        }
        
        Optional<Topico> optionalTopico = topicoRepository.findById(id);
        if (optionalTopico.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Topico topico = optionalTopico.get();
        topico.setTitulo(datos.getTitulo());
        topico.setMensaje(datos.getMensaje());
        topico.setAutor(datos.getAutor());
        topico.setCurso(datos.getCurso());
        
        Topico topicoActualizado = topicoRepository.save(topico);
        logger.info("Tópico actualizado exitosamente: {}", id);
        
        return ResponseEntity.ok(new DetalleTopicoDTO(topicoActualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un tópico")
    public ResponseEntity<?> eliminarTopico(@PathVariable Long id) {
        logger.info("Eliminando tópico con ID: {}", id);
        
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body("ID inválido");
        }
        
        Optional<Topico> topico = topicoRepository.findById(id);
        if (topico.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        topicoRepository.deleteById(id);
        logger.info("Tópico eliminado exitosamente: {}", id);
        
        return ResponseEntity.ok("Tópico eliminado correctamente");
    }
}
