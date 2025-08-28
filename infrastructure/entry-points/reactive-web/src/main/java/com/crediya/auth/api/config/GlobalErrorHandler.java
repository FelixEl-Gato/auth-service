package com.crediya.auth.api.config;

import com.crediya.auth.api.dto.ErrorResponseDTO;
import com.crediya.auth.usecase.exception.DuplicateEmailException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@Component
@Order(-2)
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
        HttpStatus status = resolveStatus(throwable);

        if (status.is4xxClientError()) {
            // Errores esperables del cliente: WARN sin stacktrace
            log.warn("{} {} -> {} {} : {}",
                    exchange.getRequest().getMethod(),
                    exchange.getRequest().getPath(),
                    status.value(), status.getReasonPhrase(),
                    userMessage(throwable));
        } else {
            // Errores de servidor: ERROR con stacktrace
            log.error("{} {} -> {} {} : {}",
                    exchange.getRequest().getMethod(),
                    exchange.getRequest().getPath(),
                    status.value(), status.getReasonPhrase(),
                    throwable.getMessage(), throwable);
        }

        ErrorResponseDTO payload = new ErrorResponseDTO(
                Instant.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                userMessage(throwable)
        );

        ServerHttpResponse resp = exchange.getResponse();
        resp.setStatusCode(status);
        resp.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            DataBuffer buf = resp.bufferFactory().wrap(om.writeValueAsBytes(payload));
            return resp.writeWith(Mono.just(buf));
        } catch (Exception writeErr) {
            return Mono.error(writeErr);
        }
    }

    private HttpStatus resolveStatus(Throwable ex) {
        // 409 por regla de negocio
        if (ex instanceof DuplicateEmailException) return HttpStatus.CONFLICT;

        // 400 por errores de entrada/validación
        if (ex instanceof IllegalArgumentException) return HttpStatus.BAD_REQUEST;
        if (ex instanceof ConstraintViolationException) return HttpStatus.BAD_REQUEST;
        if (ex instanceof ServerWebInputException) return HttpStatus.BAD_REQUEST;

        if (ex instanceof ResponseStatusException rse) return HttpStatus.valueOf(rse.getStatusCode().value());

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String userMessage(Throwable ex) {
        // No exponer detalles internos en 500
        if (resolveStatus(ex).is5xxServerError()) return "Internal server error";

        // Usar mensajes de las excepciones
        if (ex instanceof ResponseStatusException rse) return rse.getReason();

        return ex.getMessage();
    }
}
