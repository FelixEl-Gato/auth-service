package com.crediya.auth.model.usuario.gateways;

import reactor.core.publisher.Mono;

public interface TransactionPort {
    <T> Mono<T> withTransaction(Mono<T> work);
}
