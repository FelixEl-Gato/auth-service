package com.crediya.auth.r2dbc;

import com.crediya.auth.model.usuario.User;
import com.crediya.auth.model.usuario.gateways.UserRepository;
import com.crediya.auth.r2dbc.entity.UserEntity;
import com.crediya.auth.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        User,
        UserEntity,
        Long,
        UserReactiveRepository> implements UserRepository {

    private final TransactionalOperator transactionalOperator;

    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.map(d, User.class));
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<User> saveUserTransactional(User user) {
        return this.save(user).as(transactionalOperator::transactional);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmailIgnoreCase(email);
    }
}
