package io.github.carlossmariano.comunex.usuario.controller;

import io.github.carlossmariano.comunex.usuario.dto.request.AtualizarUsuarioRequest;
import io.github.carlossmariano.comunex.usuario.dto.request.CadastroUsuarioRequest;
import io.github.carlossmariano.comunex.usuario.dto.request.LoginRequest;
import io.github.carlossmariano.comunex.usuario.dto.response.AuthResponse;
import io.github.carlossmariano.comunex.usuario.dto.response.UsuarioResponse;
import io.github.carlossmariano.comunex.usuario.entity.Usuario;
import io.github.carlossmariano.comunex.usuario.service.AuthService;
import io.github.carlossmariano.comunex.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody CadastroUsuarioRequest request){
        UsuarioResponse response = usuarioService.cadastrar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> buscaPorEmail(@AuthenticationPrincipal UserDetails userDetails){
        UsuarioResponse response = usuarioService.buscarPorEmail(userDetails.getUsername());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirConta(@AuthenticationPrincipal UserDetails userDetails){
        usuarioService.deletarConta(userDetails.getUsername());
    }

    @PatchMapping("/me")
    public ResponseEntity<UsuarioResponse> atualizarConta(@Valid @RequestBody AtualizarUsuarioRequest request,
                                                          @AuthenticationPrincipal UserDetails userDetails){
        UsuarioResponse response = usuarioService.atualizarPerfil(
                userDetails.getUsername(),
                request);

        return ResponseEntity.ok(response);
    }
}
