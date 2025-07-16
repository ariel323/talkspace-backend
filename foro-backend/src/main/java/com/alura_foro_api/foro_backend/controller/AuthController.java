package com.alura_foro_api.foro_backend.controller;

import com.alura_foro_api.foro_backend.dto.RegistroUsuarioDTO;
import com.alura_foro_api.foro_backend.model.Usuario;
import com.alura_foro_api.foro_backend.repository.UsuarioRepository;
import com.alura_foro_api.foro_backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;
import java.util.Optional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "API para autenticación de usuarios")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente")
    @ApiResponse(responseCode = "400", description = "El usuario ya existe")
    public ResponseEntity<?> register(@RequestBody @Valid RegistroUsuarioDTO datos) {
        logger.info("Intentando registrar usuario: {}", datos.getUsername());
        
        if (usuarioRepository.existsByUsername(datos.getUsername())) {
            logger.warn("Usuario ya existe: {}", datos.getUsername());
            return ResponseEntity.badRequest().body(Map.of(
                "error", "El usuario ya existe"
            ));
        }
        
        Usuario usuario = new Usuario();
        usuario.setUsername(datos.getUsername());
        usuario.setPassword(passwordEncoder.encode(datos.getPassword()));
        usuarioRepository.save(usuario);
        
        String token = jwtUtil.generateToken(usuario.getUsername());
        logger.info("Usuario registrado y token generado: {}", usuario.getUsername());
        
        return ResponseEntity.ok(Map.of(
            "token", token,
            "username", usuario.getUsername()
        ));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve un token JWT")
    @ApiResponse(responseCode = "200", description = "Login exitoso")
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    public ResponseEntity<?> login(@RequestBody @Valid RegistroUsuarioDTO datos) {
        logger.info("Intento de login para usuario: {}", datos.getUsername());
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(datos.getUsername());
        
        if (usuarioOpt.isEmpty() || !passwordEncoder.matches(datos.getPassword(), usuarioOpt.get().getPassword())) {
            logger.warn("Login fallido para usuario: {}", datos.getUsername());
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
        
        String token = jwtUtil.generateToken(usuarioOpt.get().getUsername());
        logger.info("Token generado para usuario: {}", usuarioOpt.get().getUsername());
        
        return ResponseEntity.ok(Map.of(
            "token", token,
            "username", usuarioOpt.get().getUsername()
        ));
    }
}
