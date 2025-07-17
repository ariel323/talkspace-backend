package com.alura_foro_api.foro_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/test")
@Tag(name = "Test", description = "Controlador de prueba")
public class TopicoControllerSimple {

    @GetMapping("/hello")
    @Operation(summary = "Test endpoint")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("API funcionando correctamente");
    }
}
