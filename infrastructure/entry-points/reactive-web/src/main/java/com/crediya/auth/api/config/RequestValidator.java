package com.crediya.auth.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final SmartValidator validator; // viene de spring-boot-starter-validation

    public <T> Mono<T> validate(T target, Class<?>... groups) {
        var errors = new BeanPropertyBindingResult(target, target.getClass().getSimpleName());
        validator.validate(target, errors, (Object) groups);
        if (errors.hasErrors()) {
            var msg = errors.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage() != null ? e.getDefaultMessage() : e.toString())
                    .collect(Collectors.joining("; "));
            return Mono.error(new ServerWebInputException(msg));
        }
        return Mono.just(target);
    }
}