package com.crediya.auth.usecase.maintainuser;

import com.crediya.auth.model.usuario.User;
import com.crediya.auth.model.usuario.gateways.TransactionPort;
import com.crediya.auth.model.usuario.gateways.UserRepository;
import com.crediya.auth.usecase.exception.DuplicateEmailException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class MaintainUserUseCase {

    private final UserRepository userRepository;

    private static final BigDecimal  MAX_BASE_SALARY = new BigDecimal("15000000");
    private static final BigDecimal  MIN_BASE_SALARY = BigDecimal.ZERO;

    public Mono<User> create(User user) {
        return Mono.defer(() -> validateUser(user))
                .flatMap(validUser ->
                        userRepository.existsByEmail(validUser.getEmail())
                                .flatMap(exists -> Boolean.TRUE.equals(exists)
                                        ? Mono.error(new DuplicateEmailException(validUser.getEmail()))
                                        : userRepository.saveUserTransactional(validUser)
                                )
                );
    }

    private Mono<User> validateUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            return Mono.error(new IllegalArgumentException("name is required"));
        }
        if (user.getLastName() == null || user.getLastName().isEmpty()) {
            return Mono.error(new IllegalArgumentException("lastName is required"));
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return Mono.error(new IllegalArgumentException("email is required"));
        }
        if(!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return Mono.error(new IllegalArgumentException("email format is invalid"));
        }
        if (user.getBaseSalary() == null
                || user.getBaseSalary().compareTo(MIN_BASE_SALARY) <= 0
                ||  user.getBaseSalary().compareTo(MAX_BASE_SALARY) >= 0) {
            return Mono.error(new IllegalArgumentException("baseSalary must be greater than " + MIN_BASE_SALARY + " and less than " + MAX_BASE_SALARY));
        }
        return Mono.just(user);
    }
}
