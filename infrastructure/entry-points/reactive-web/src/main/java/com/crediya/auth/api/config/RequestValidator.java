package com.crediya.auth.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final SmartValidator validator;

    public <T> Mono<T> validate(T target) {
        Errors errors = new BeanPropertyBindingResult(target, target.getClass().getName());
        validator.validate(target, errors);
        if (errors.hasErrors()) {
            String msg = errors.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage() != null ? e.getDefaultMessage() : e.toString())
                    .collect(Collectors.joining("; "));
            return Mono.error(new ServerWebInputException(msg));
        }
        return Mono.just(target);
    }
}