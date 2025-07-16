package com.alura_foro_api.foro_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Respuesta de error estándar de la API")
public class ErrorResponseDTO {
    
    @Schema(description = "Código de estado HTTP", example = "400")
    private int codigo;
    
    @Schema(description = "Mensaje principal del error", example = "Error de validación")
    private String mensaje;
    
    @Schema(description = "Descripción detallada del error", example = "Los datos proporcionados no son válidos")
    private String descripcion;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Fecha y hora del error", example = "2025-07-16 10:30:00")
    private LocalDateTime timestamp;
    
    @Schema(description = "Ruta donde ocurrió el error", example = "/api/v1/topicos")
    private String path;
    
    @Schema(description = "Lista de errores específicos de validación")
    private List<String> errores;

    // Constructores
    public ErrorResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponseDTO(int codigo, String mensaje, String descripcion, String path) {
        this();
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.descripcion = descripcion;
        this.path = path;
    }

    public ErrorResponseDTO(int codigo, String mensaje, String descripcion, String path, List<String> errores) {
        this(codigo, mensaje, descripcion, path);
        this.errores = errores;
    }

    // Getters y Setters
    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public List<String> getErrores() { return errores; }
    public void setErrores(List<String> errores) { this.errores = errores; }
}
