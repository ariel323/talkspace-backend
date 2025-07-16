package com.alura_foro_api.foro_backend.exception;

public class TopicoYaExisteException extends RuntimeException {
    public TopicoYaExisteException(String mensaje) {
        super(mensaje);
    }
    
    public TopicoYaExisteException(String titulo, String mensaje) {
        super(String.format("Ya existe un tópico con el título '%s' y mensaje similar", titulo));
    }
}
