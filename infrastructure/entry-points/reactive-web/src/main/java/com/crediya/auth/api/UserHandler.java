package com.crediya.auth.api;

import com.crediya.auth.api.config.RequestValidator;
import com.crediya.auth.api.dto.UserCreateDTO;
import com.crediya.auth.api.mapper.UserDTOMapper;
import com.crediya.auth.usecase.maintainuser.MaintainUserUseCase;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserHandler {

    private final MaintainUserUseCase maintainUserUseCase;
    private final UserDTOMapper userDTOMapper;
    private final RequestValidator requestValidator;

    public Mono<ServerResponse> listenCreateUser(ServerRequest serverRequest) {

        long started = System.currentTimeMillis();
        log.info("POST /api/v1/usuarios - request received");

        return serverRequest.bodyToMono(UserCreateDTO.class)
                .flatMap(userCreateDTO -> requestValidator.validate(userCreateDTO))
                .map(userDTOMapper::toModel)
                .flatMap(maintainUserUseCase::create)
                .map(userDTOMapper::toResponse)
                .flatMap(createdUser -> {
                    log.info("POST /api/v1/usuarios - created in {} ms", System.currentTimeMillis() - started);
                    return ServerResponse.ok().bodyValue(createdUser);
                })
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body is required")));
    }
}
