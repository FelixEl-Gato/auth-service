package com.crediya.auth.api.config;

import com.crediya.auth.usecase.exception.DuplicateEmailException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
@Order(-2)
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = resolveStatus(ex);

        if (status.is4xxClientError()) {
            // Errores esperables del cliente: WARN sin stacktrace
            log.warn("{} {} -> {} {} : {}",
                    exchange.getRequest().getMethod(),
                    exchange.getRequest().getPath(),
                    status.value(), status.getReasonPhrase(),
                    userMessage(ex));
        } else {
            // Errores de servidor: ERROR con stacktrace
            log.error("{} {} -> {} {} : {}",
                    exchange.getRequest().getMethod(),
                    exchange.getRequest().getPath(),
                    status.value(), status.getReasonPhrase(),
                    ex.getMessage(), ex);
        }

        Map<String,Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", userMessage(ex),
                "path", exchange.getRequest().getPath().value()
        );

        var resp = exchange.getResponse();
        resp.setStatusCode(status);
        resp.getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        try {
            var buf = resp.bufferFactory().wrap(om.writeValueAsBytes(body));
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
        if (ex instanceof org.springframework.web.server.ServerWebInputException) return HttpStatus.BAD_REQUEST;

        if (ex instanceof ResponseStatusException rse) return HttpStatus.valueOf(rse.getStatusCode().value());

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String userMessage(Throwable ex) {
        // No exponer detalles internos en 500
        if (resolveStatus(ex).is5xxServerError()) return "Internal server error";

        // Usar mensajes de las excepciones
        if(ex instanceof ResponseStatusException rse) return rse.getReason();

        return ex.getMessage();
    }
}
