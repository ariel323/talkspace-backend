package com.alura_foro_api.foro_backend.controller;

import com.alura_foro_api.foro_backend.dto.RegistroUsuarioDTO;
import com.alura_foro_api.foro_backend.model.Usuario;
import com.alura_foro_api.foro_backend.model.Topico;
import com.alura_foro_api.foro_backend.repository.UsuarioRepository;
import com.alura_foro_api.foro_backend.repository.TopicoRepository;
import com.alura_foro_api.foro_backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

import com.alura_foro_api.foro_backend.dto.DetalleTopicoDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TopicoRepository topicoRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

   @PostMapping("/register")
public ResponseEntity<?> register(@RequestBody @Valid RegistroUsuarioDTO datos) {
    System.out.println("Intentando registrar usuario: " + datos.getUsername());
    if (usuarioRepository.existsByUsername(datos.getUsername())) {
        System.out.println("Usuario ya existe: " + datos.getUsername());
        return ResponseEntity.badRequest().body(Map.of(
            "error", "El usuario ya existe"
        ));
    }
    Usuario usuario = new Usuario();
    usuario.setUsername(datos.getUsername());
    usuario.setPassword(passwordEncoder.encode(datos.getPassword()));
    usuarioRepository.save(usuario);
    String token = jwtUtil.generateToken(usuario.getUsername());
    System.out.println("Usuario registrado y token generado: " + usuario.getUsername());
    return ResponseEntity.ok(Map.of(
        "token", token,
        "username", usuario.getUsername()
    ));
}

    @GetMapping("/topicos/buscar")
    public ResponseEntity<?> buscarPorTituloYAutor(
            @RequestParam String titulo,
            @RequestParam String autor) {
        Optional<Topico> topico = topicoRepository.findByTituloAndAutor(titulo, autor);
        if (topico.isPresent()) {
            return ResponseEntity.ok(new DetalleTopicoDTO(topico.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid RegistroUsuarioDTO datos) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(datos.getUsername());
        if (usuarioOpt.isEmpty() || !passwordEncoder.matches(datos.getPassword(), usuarioOpt.get().getPassword())) {
            System.out.println("Login fallido para usuario: " + datos.getUsername());
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
        String token = jwtUtil.generateToken(usuarioOpt.get().getUsername());
        System.out.println("Token generado para usuario: " + usuarioOpt.get().getUsername() + " -> " + token);
        return ResponseEntity.ok(Map.of(
            "token", token,
            "username", usuarioOpt.get().getUsername()
        ));
    }
}
