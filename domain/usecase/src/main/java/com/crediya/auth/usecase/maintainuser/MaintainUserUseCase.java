package com.crediya.auth.usecase.maintainuser;

import com.crediya.auth.model.usuario.Usuario;
import com.crediya.auth.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class MaintainUserUseCase {

    private final UsuarioRepository usuarioRepository;

    public Mono<Usuario> create(Usuario usuario) {
        return Mono.defer(() -> validateUser(usuario))
                .flatMap(validUser ->
                        usuarioRepository.existsByEmail(validUser.getEmail())
                                .flatMap(exists -> exists
                                        ? Mono.error(new IllegalArgumentException("Email already exists"))
                                        : usuarioRepository.save(validUser)
                                )
                );
    }

    private Mono<Usuario> validateUser(Usuario usuario) {
        if (usuario.getName() == null || usuario.getName().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Name is required"));
        }
        if (usuario.getLastName() == null || usuario.getLastName().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Last name is required"));
        }
        if (usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Email is required"));
        }
        if (usuario.getBaseSalary() == null
                || usuario.getBaseSalary().doubleValue() <= 0
                || usuario.getBaseSalary().doubleValue() >= 15_000_000) {
            return Mono.error(new IllegalArgumentException("Base salary must be greater than zero and less than 15,000,000"));
        }
        return Mono.just(usuario);
    }
}
