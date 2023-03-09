package ca.nbc.digital.reatil.geekshow;

import ca.nbc.digital.reatil.geekshow.domain.Address;
import ca.nbc.digital.reatil.geekshow.domain.User;
import ca.nbc.digital.reatil.geekshow.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.created;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

public class UserHandler {
    private final UserRepository userRepository;

    public UserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(User.class)
                .flatMap(userRepository::save)
                .flatMap(user -> created(getLocation(request, user)).build());
    }


    public Mono<ServerResponse> list(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(userRepository.findAll(), User.class);
    }

    public Mono<ServerResponse> get(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(userRepository.findById(request.pathVariable("id")), User.class);
    }

    public Mono<ServerResponse> addAddress(ServerRequest request) {
        return request.bodyToMono(Address.class)
                .flatMap(address -> userRepository.findById(request.pathVariable("id"))
                        .map(user -> addAddress(address, user)))
                .flatMap(userRepository::save)
                .flatMap(user -> created(getLocation(request, user)).build());
    }

    @NotNull
    private static User addAddress(Address address, User user) {
        if (!user.hasMajority()) {
            throw new IllegalArgumentException("User must be 18 years old to add an address");
        }
        user.add(address);
        return user;
    }

    @NotNull
    private static URI getLocation(ServerRequest request, User user) {
        URI uri = request.exchange().getRequest().getURI();
        return fromUriString(uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort() + "/users/" + user.id()).build()
                .toUri();
    }
}
