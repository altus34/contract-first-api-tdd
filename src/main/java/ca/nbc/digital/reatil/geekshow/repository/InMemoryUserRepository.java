package ca.nbc.digital.reatil.geekshow.repository;

import ca.nbc.digital.reatil.geekshow.domain.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new HashMap<>();

    @Override
    public Mono<User> save(User user) {
        users.put(user.id(), user);
        return Mono.just(user);
    }

    @Override
    public Flux<User> findAll() {
        return Flux.fromIterable(users.values());
    }

    @Override
    public Mono<User> findById(String id) {
        return Mono.just(users.get(id));
    }

    @Override
    public Mono<Void> deleteAll() {
        users.clear();
        return Mono.empty();
    }
}
