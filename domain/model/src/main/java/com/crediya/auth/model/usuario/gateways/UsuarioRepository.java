package com.crediya.auth.model.usuario.gateways;

import com.crediya.auth.model.usuario.Usuario;
import reactor.core.publisher.Mono;

public interface UsuarioRepository {

    Mono<Usuario> save(Usuario usuario);
    Mono<Boolean> existsByEmail(String email);
}
