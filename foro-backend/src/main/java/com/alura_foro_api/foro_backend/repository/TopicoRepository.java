package com.alura_foro_api.foro_backend.repository;

import com.alura_foro_api.foro_backend.model.Topico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repositorio para la entidad Topico
 * Proporciona métodos de acceso a datos para la gestión de tópicos del foro
 * 
 * @author TalkSpace Backend Team
 * @version 1.0
 */
@Repository
public interface TopicoRepository extends JpaRepository<Topico, Long> {
    
    // ===============================================
    // MÉTODOS DE BÚSQUEDA EXACTA
    // ===============================================
    
    /**
     * Busca un tópico por título y mensaje exactos
     * Utilizado para detectar duplicados
     * 
     * @param titulo el título del tópico
     * @param mensaje el mensaje del tópico
     * @return Optional con el tópico si existe
     */
    Optional<Topico> findByTituloAndMensaje(String titulo, String mensaje);
    
    /**
     * Busca un tópico por título y autor exactos
     * 
     * @param titulo el título del tópico
     * @param autor el autor del tópico
     * @return Optional con el tópico si existe
     */
    Optional<Topico> findByTituloAndAutor(String titulo, String autor);
    
    // ===============================================
    // MÉTODOS DE FILTRADO SIMPLE
    // ===============================================
    
    /**
     * Encuentra tópicos por curso específico con paginación
     * 
     * @param curso el nombre del curso
     * @param pageable configuración de paginación
     * @return página de tópicos del curso especificado
     */
    Page<Topico> findByCurso(String curso, Pageable pageable);
    
    /**
     * Encuentra tópicos por autor específico con paginación
     * 
     * @param autor el nombre del autor
     * @param pageable configuración de paginación
     * @return página de tópicos del autor especificado
     */
    Page<Topico> findByAutor(String autor, Pageable pageable);
    
    // ===============================================
    // MÉTODOS DE BÚSQUEDA CON CONTENIDO PARCIAL
    // ===============================================
    
    /**
     * Busca tópicos cuyo título contenga el texto especificado (case-insensitive)
     * 
     * @param titulo texto a buscar en el título
     * @param pageable configuración de paginación
     * @return página de tópicos que coinciden con la búsqueda
     */
    Page<Topico> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);
    
    /**
     * Busca tópicos cuyo curso contenga el texto especificado (case-insensitive)
     * 
     * @param curso texto a buscar en el curso
     * @param pageable configuración de paginación
     * @return página de tópicos que coinciden con la búsqueda
     */
    Page<Topico> findByCursoContainingIgnoreCase(String curso, Pageable pageable);
    
    /**
     * Busca tópicos cuyo autor contenga el texto especificado (case-insensitive)
     * 
     * @param autor texto a buscar en el autor
     * @param pageable configuración de paginación
     * @return página de tópicos que coinciden con la búsqueda
     */
    Page<Topico> findByAutorContainingIgnoreCase(String autor, Pageable pageable);
    
    // ===============================================
    // MÉTODOS DE FILTRADO POR FECHA
    // ===============================================
    
    /**
     * Encuentra tópicos de un curso específico dentro de un rango de fechas
     * 
     * @param curso el nombre del curso
     * @param start fecha de inicio del rango
     * @param end fecha de fin del rango
     * @param pageable configuración de paginación
     * @return página de tópicos que coinciden con los criterios
     */
    Page<Topico> findByCursoAndFechaCreacionBetween(
        String curso, 
        LocalDateTime start, 
        LocalDateTime end, 
        Pageable pageable
    );
    
    /**
     * Encuentra tópicos creados en un rango de fechas específico
     * 
     * @param start fecha de inicio del rango
     * @param end fecha de fin del rango
     * @param pageable configuración de paginación
     * @return página de tópicos en el rango de fechas
     */
    Page<Topico> findByFechaCreacionBetween(
        LocalDateTime start, 
        LocalDateTime end, 
        Pageable pageable
    );
    
    // ===============================================
    // CONSULTAS PERSONALIZADAS AVANZADAS
    // ===============================================
    
    /**
     * Búsqueda avanzada con múltiples filtros opcionales
     * Permite combinar criterios de título, curso, autor y fechas
     * 
     * @param titulo filtro por título (opcional, búsqueda parcial)
     * @param curso filtro por curso (opcional, búsqueda parcial)
     * @param autor filtro por autor (opcional, búsqueda parcial)
     * @param fechaInicio fecha mínima de creación (opcional)
     * @param fechaFin fecha máxima de creación (opcional)
     * @param pageable configuración de paginación
     * @return página de tópicos que coinciden con los filtros aplicados
     */
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
        Pageable pageable
    );
    
    /**
     * Busca tópicos por múltiples palabras clave en título y mensaje
     * 
     * @param palabrasClave texto a buscar en título y mensaje
     * @param pageable configuración de paginación
     * @return página de tópicos que contienen las palabras clave
     */
    @Query("SELECT t FROM Topico t WHERE " +
           "LOWER(t.titulo) LIKE LOWER(CONCAT('%', :palabrasClave, '%')) OR " +
           "LOWER(t.mensaje) LIKE LOWER(CONCAT('%', :palabrasClave, '%'))")
    Page<Topico> buscarPorPalabrasClave(
        @Param("palabrasClave") String palabrasClave, 
        Pageable pageable
    );
    
    /**
     * Obtiene los tópicos más recientes de un autor específico
     * 
     * @param autor el nombre del autor
     * @param limite número máximo de resultados
     * @return lista de tópicos más recientes del autor
     */
    @Query("SELECT t FROM Topico t WHERE t.autor = :autor ORDER BY t.fechaCreacion DESC")
    Page<Topico> findTopicosRecientesPorAutor(
        @Param("autor") String autor, 
        Pageable pageable
    );
    
    /**
     * Cuenta el número total de tópicos por curso
     * 
     * @param curso el nombre del curso
     * @return número total de tópicos en el curso
     */
    long countByCurso(String curso);
    
    /**
     * Cuenta el número total de tópicos por autor
     * 
     * @param autor el nombre del autor
     * @return número total de tópicos del autor
     */
    long countByAutor(String autor);
    
    // ===============================================
    // CONSULTAS DE ESTADÍSTICAS
    // ===============================================
    
    /**
     * Obtiene los cursos más activos (con más tópicos)
     * 
     * @param pageable configuración de paginación
     * @return lista de cursos ordenados por número de tópicos
     */
    @Query("SELECT t.curso, COUNT(t) as total FROM Topico t " +
           "GROUP BY t.curso ORDER BY total DESC")
    Page<Object[]> findCursosMasActivos(Pageable pageable);
    
    /**
     * Obtiene los autores más activos (con más tópicos)
     * 
     * @param pageable configuración de paginación
     * @return lista de autores ordenados por número de tópicos
     */
    @Query("SELECT t.autor, COUNT(t) as total FROM Topico t " +
           "GROUP BY t.autor ORDER BY total DESC")
    Page<Object[]> findAutoresMasActivos(Pageable pageable);
}