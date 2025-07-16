package com.alura_foro_api.foro_backend.exception;

public class AccesoNoAutorizadoException extends RuntimeException {
    public AccesoNoAutorizadoException(String mensaje) {
        super(mensaje);
    }
    
    public AccesoNoAutorizadoException() {
        super("No tienes permisos para realizar esta operación");
    }
}
