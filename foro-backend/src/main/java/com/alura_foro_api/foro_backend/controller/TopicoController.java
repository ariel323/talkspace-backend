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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/topicos")
@Tag(name = "Tópicos", description = "API para gestión de tópicos del foro")
public class TopicoController {

    private static final Logger logger = LoggerFactory.getLogger(TopicoController.class);

    @Autowired
    private TopicoRepository topicoRepository;

    @Operation(summary = "Crear un nuevo tópico", description = "Registra un nuevo tópico en el foro")
    @ApiResponse(responseCode = "200", description = "Tópico creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o tópico duplicado")
    @CacheEvict(value = "topicos", allEntries = true)
    @PostMapping
    public ResponseEntity<?> registrarTopico(@RequestBody @Valid RegistroTopicoDTO datos) {
        logger.info("Intentando registrar nuevo tópico: {}", datos.getTitulo());
        
        if (topicoRepository.findByTituloAndMensaje(datos.getTitulo(), datos.getMensaje()).isPresent()) {
            logger.warn("Intento de crear tópico duplicado: {}", datos.getTitulo());
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

    @Operation(summary = "Listar tópicos", description = "Obtiene una lista paginada de tópicos")
    @ApiResponse(responseCode = "200", description = "Lista de tópicos obtenida exitosamente")
    @Cacheable(value = "topicos", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    @GetMapping
    public ResponseEntity<Page<DetalleTopicoDTO>> listarTopicos(
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable) {
        logger.info("Listando tópicos - página: {}, tamaño: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Topico> topicos = topicoRepository.findAll(pageable);
        Page<DetalleTopicoDTO> detalleTopicos = topicos.map(DetalleTopicoDTO::new);
        return ResponseEntity.ok(detalleTopicos);
    }

    @Operation(summary = "Obtener tópico por ID", description = "Obtiene los detalles de un tópico específico")
    @ApiResponse(responseCode = "200", description = "Tópico encontrado")
    @ApiResponse(responseCode = "404", description = "Tópico no encontrado")
    @Cacheable(value = "topico", key = "#id")
    @GetMapping("/{id}")
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

    @Operation(summary = "Actualizar tópico", description = "Actualiza los datos de un tópico existente")
    @ApiResponse(responseCode = "200", description = "Tópico actualizado exitosamente")
    @ApiResponse(responseCode = "404", description = "Tópico no encontrado")
    @CachePut(value = "topico", key = "#id")
    @CacheEvict(value = "topicos", allEntries = true)
    @PutMapping("/{id}")
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

    @Operation(summary = "Eliminar tópico", description = "Elimina un tópico del foro")
    @ApiResponse(responseCode = "200", description = "Tópico eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Tópico no encontrado")
    @CacheEvict(value = {"topico", "topicos"}, key = "#id", allEntries = true)
    @DeleteMapping("/{id}")
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
        return ResponseEntity.ok("Tópico eliminado correctamente");
    }

    @Operation(summary = "Buscar tópicos", description = "Busca tópicos por título, curso o autor")
    @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente")
    @GetMapping("/search")
    public ResponseEntity<Page<DetalleTopicoDTO>> buscarTopicos(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String curso,
            @RequestParam(required = false) String autor,
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable) {
        
        logger.info("Buscando tópicos con filtros - título: {}, curso: {}, autor: {}", titulo, curso, autor);
        
        // Crear ejemplo para búsqueda
        Topico ejemplo = new Topico();
        if (titulo != null && !titulo.trim().isEmpty()) {
            ejemplo.setTitulo(titulo);
        }
        if (curso != null && !curso.trim().isEmpty()) {
            ejemplo.setCurso(curso);
        }
        if (autor != null && !autor.trim().isEmpty()) {
            ejemplo.setAutor(autor);
        }
        
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        
        Example<Topico> example = Example.of(ejemplo, matcher);
        Page<Topico> topicos = topicoRepository.findAll(example, pageable);
        Page<DetalleTopicoDTO> detalleTopicos = topicos.map(DetalleTopicoDTO::new);
        
        return ResponseEntity.ok(detalleTopicos);
    }

    @Operation(summary = "Búsqueda avanzada", description = "Búsqueda avanzada con filtros de fecha")
    @ApiResponse(responseCode = "200", description = "Búsqueda avanzada realizada exitosamente")
    @GetMapping("/advanced-search")
    public ResponseEntity<Page<DetalleTopicoDTO>> busquedaAvanzada(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String curso,
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable) {
        
        logger.info("Búsqueda avanzada - título: {}, curso: {}, autor: {}, fechas: {} a {}", 
                   titulo, curso, autor, fechaInicio, fechaFin);
        
        LocalDateTime inicio = null;
        LocalDateTime fin = null;
        
        try {
            if (fechaInicio != null && !fechaInicio.trim().isEmpty()) {
                inicio = LocalDateTime.parse(fechaInicio + "T00:00:00");
            }
            if (fechaFin != null && !fechaFin.trim().isEmpty()) {
                fin = LocalDateTime.parse(fechaFin + "T23:59:59");
            }
        } catch (Exception e) {
            logger.error("Error al parsear fechas: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
        
        Page<Topico> topicos = topicoRepository.buscarConFiltros(titulo, curso, autor, inicio, fin, pageable);
        Page<DetalleTopicoDTO> detalleTopicos = topicos.map(DetalleTopicoDTO::new);
        
        return ResponseEntity.ok(detalleTopicos);
    }

    @Operation(summary = "Filtrar por curso", description = "Obtiene tópicos de un curso específico")
    @Cacheable(value = "topicosPorCurso", key = "#curso + '-' + #pageable.pageNumber")
    @GetMapping("/curso/{curso}")
    public ResponseEntity<Page<DetalleTopicoDTO>> filtrarPorCurso(
            @PathVariable String curso,
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable) {
        
        logger.info("Filtrando tópicos por curso: {}", curso);
        Page<Topico> topicos = topicoRepository.findByCurso(curso, pageable);
        Page<DetalleTopicoDTO> detalleTopicos = topicos.map(DetalleTopicoDTO::new);
        
        return ResponseEntity.ok(detalleTopicos);
    }

    @Operation(summary = "Filtrar por autor", description = "Obtiene tópicos de un autor específico")
    @Cacheable(value = "topicosPorAutor", key = "#autor + '-' + #pageable.pageNumber")
    @GetMapping("/autor/{autor}")
    public ResponseEntity<Page<DetalleTopicoDTO>> filtrarPorAutor(
            @PathVariable String autor,
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable) {
        
        logger.info("Filtrando tópicos por autor: {}", autor);
        Page<Topico> topicos = topicoRepository.findByAutor(autor, pageable);
        Page<DetalleTopicoDTO> detalleTopicos = topicos.map(DetalleTopicoDTO::new);
        
        return ResponseEntity.ok(detalleTopicos);
    }
}
