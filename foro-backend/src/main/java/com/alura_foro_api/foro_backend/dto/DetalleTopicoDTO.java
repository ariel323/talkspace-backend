package com.alura_foro_api.foro_backend.dto;

import com.alura_foro_api.foro_backend.model.Topico;
import java.time.LocalDateTime;

public class DetalleTopicoDTO {
    private String titulo;
    private String mensaje;
    private LocalDateTime fechaCreacion;
    private String estado;
    private String autor;
    private String curso;

    public DetalleTopicoDTO(Topico topico) {
        this.titulo = topico.getTitulo();
        this.mensaje = topico.getMensaje();
        this.fechaCreacion = topico.getFechaCreacion();
        this.estado = topico.getEstado();
        this.autor = topico.getAutor();
        this.curso = topico.getCurso();
    }

// getters y setters

    public String getTitulo() {
        return titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getAutor() {
        return autor;
    }

    public String getCurso() {
        return curso;
    }

    public java.time.LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public void setFechaCreacion(java.time.LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
