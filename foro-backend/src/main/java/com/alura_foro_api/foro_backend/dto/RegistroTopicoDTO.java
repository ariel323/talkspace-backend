package com.alura_foro_api.foro_backend.dto;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para el registro de un nuevo tópico")
public class RegistroTopicoDTO {
    
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 100, message = "El título debe tener entre 5 y 100 caracteres")
    @Schema(description = "Título del tópico", example = "¿Cómo implementar Spring Security?", required = true)
    private String titulo;
    
    @NotBlank(message = "El mensaje es obligatorio")
    @Size(min = 10, max = 2000, message = "El mensaje debe tener entre 10 y 2000 caracteres")
    @Schema(description = "Contenido del mensaje", example = "Necesito ayuda para configurar autenticación JWT...", required = true)
    private String mensaje;
    
    @NotBlank(message = "El autor es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre del autor debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "El autor solo puede contener letras, números, puntos, guiones y guiones bajos")
    @Schema(description = "Nombre del autor", example = "usuarioEjemplo", required = true)
    private String autor;
    
    @NotBlank(message = "El curso es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre del curso debe tener entre 2 y 50 caracteres")
    @Schema(description = "Curso relacionado", example = "Spring Boot", required = true)
    private String curso;

    // Constructores
    public RegistroTopicoDTO() {}

    public RegistroTopicoDTO(String titulo, String mensaje, String autor, String curso) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.autor = autor;
        this.curso = curso;
    }

    // Getters y setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
}
