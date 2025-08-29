package com.crediya.auth.usecase.maintainuser;

import com.crediya.auth.model.usuario.User;
import com.crediya.auth.model.usuario.gateways.UserRepository;
import com.crediya.auth.usecase.exception.DuplicateEmailException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintainUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    private MaintainUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new MaintainUserUseCase(userRepository);
    }

    private User validUser() {
        User u = new User();
        u.setName("John");
        u.setLastName("Doe");
        u.setEmail("alice@example.com");
        u.setBaseSalary(new BigDecimal("1000"));

        return u;
    }

    private User withEmail(User base, String email) {
        base.setEmail(email);
        return base;
    }

    private User withName(User base, String name) {
        base.setName(name);
        return base;
    }

    private User withLastName(User base, String lastName) {
        base.setLastName(lastName);
        return base;
    }

    private User withSalary(User base, BigDecimal salary) {
        base.setBaseSalary(salary);
        return base;
    }

    @Test
    @DisplayName("flow: create Email not exists -> save and return user")
    void create_ok_whenEmailNotExists() {
        User input = validUser();
        User persisted = validUser();

        when(userRepository.existsByEmail(input.getEmail()))
                .thenReturn(Mono.just(false));
        when(userRepository.saveUserTransactional(input))
                .thenReturn(Mono.just(persisted));

        StepVerifier.create(useCase.create(input))
                .expectNext(persisted)
                .verifyComplete();

        InOrder inOrder = inOrder(userRepository);
        inOrder.verify(userRepository)
                .existsByEmail(input.getEmail());
        inOrder.verify(userRepository)
                .saveUserTransactional(input);

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("name required")
    void validate_name_required() {
        User input = withName(validUser(), null);

        StepVerifier.create(useCase.create(input))
                .expectErrorSatisfies( ex -> {
                    assert ex instanceof IllegalArgumentException;
                    assert ex.getMessage().equals("name is required");
                })
                .verify();

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("name empty")
    void validate_name_required_empty() {
        User input = withName(validUser(), "");

        StepVerifier.create(useCase.create(input))
                .expectErrorSatisfies( ex -> {
                    assert ex instanceof IllegalArgumentException;
                    assert ex.getMessage().equals("name is required");
                })
                .verify();

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("lastName required")
    void validate_lastName_required() {
        User input = withLastName(validUser(), "");

        StepVerifier.create(useCase.create(input))
                .expectErrorSatisfies( ex -> {
                    assert ex instanceof IllegalArgumentException;
                    assert ex.getMessage().equals("lastName is required");
                })
                .verify();

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("lastName null")
    void validate_lastName_required_null() {
        User input = withLastName(validUser(), null);

        StepVerifier.create(useCase.create(input))
                .expectErrorSatisfies( ex -> {
                    assert ex instanceof IllegalArgumentException;
                    assert ex.getMessage().equals("lastName is required");
                })
                .verify();

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("email required")
    void validate_email_required() {
        User input = withEmail(validUser(), null);

        StepVerifier.create(useCase.create(input))
                .expectErrorSatisfies( ex -> {
                    assert ex instanceof IllegalArgumentException;
                    assert ex.getMessage().equals("email is required");
                })
                .verify();

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("email format invalid")
    void validate_email_format() {
        User input = withEmail(validUser(), "invalid@");

        StepVerifier.create(useCase.create(input))
                .expectErrorSatisfies( ex -> {
                    assert ex instanceof IllegalArgumentException;
                    assert ex.getMessage().equals("email format is invalid");
                })
                .verify();

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("baseSalary null")
    void baseSalary_null() {
        User input = withSalary(validUser(), null);

        StepVerifier.create(useCase.create(input))
                .expectErrorSatisfies( ex -> {
                   assert ex instanceof IllegalArgumentException;
                   assert ex.getMessage().contains("must be greater than");
                })
                .verify();

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("baseSalary < zero")
    void baseSalary_zero() {
        User input = withSalary(validUser(), BigDecimal.valueOf(-1));

        StepVerifier.create(useCase.create(input))
                .expectErrorSatisfies( ex -> {
                    assert ex instanceof IllegalArgumentException;
                    assert ex.getMessage().contains("must be greater than");
                })
                .verify();

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("baseSalary > 15000000")
    void salary_greaterOrEqualMax(){
        User input = withSalary(validUser(), new BigDecimal("15000001"));

        StepVerifier.create(useCase.create(input))
                .expectErrorSatisfies( ex -> {
                    assert ex instanceof IllegalArgumentException;
                    assert ex.getMessage().contains("must be greater than");
                })
                .verify();

        verifyNoMoreInteractions(userRepository);
    }

}
