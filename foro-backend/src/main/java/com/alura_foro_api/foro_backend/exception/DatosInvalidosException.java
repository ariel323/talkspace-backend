package com.alura_foro_api.foro_backend.exception;

/**
 * Excepción lanzada cuando los datos proporcionados no son válidos
 */
public class DatosInvalidosException extends RuntimeException {
    
    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
    
    public static DatosInvalidosException idInvalido(Long id) {
        return new DatosInvalidosException("El ID proporcionado no es válido: " + id);
    }
    
    public static DatosInvalidosException parametroRequerido(String parametro) {
        return new DatosInvalidosException("El parámetro '" + parametro + "' es requerido");
    }
}
