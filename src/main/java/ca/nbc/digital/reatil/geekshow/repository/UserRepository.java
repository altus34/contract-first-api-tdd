package ca.nbc.digital.reatil.geekshow.repository;

import ca.nbc.digital.reatil.geekshow.domain.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> save(User user);

    Flux<User> findAll();

    Mono<User> findById(String id);

    Mono<Void> deleteAll();
}
