package com.crediya.auth.model.usuario.gateways;

import com.crediya.auth.model.usuario.User;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> save(User user);
    Mono<Boolean> existsByEmail(String email);
}
