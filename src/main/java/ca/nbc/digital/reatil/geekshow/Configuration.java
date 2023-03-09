package ca.nbc.digital.reatil.geekshow;

import ca.nbc.digital.reatil.geekshow.repository.InMemoryUserRepository;
import ca.nbc.digital.reatil.geekshow.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    RouterFunction<ServerResponse> routes(UserHandler userHandler) {
        return route()
                .path("/users", builder -> builder
                        .GET("", userHandler::list)
                        .GET("/{id}", userHandler::get)
                        .POST("", accept(APPLICATION_JSON), userHandler::create)
                        .PATCH("/{id}/addresses", accept(APPLICATION_JSON), userHandler::addAddress)
                )
                .build();
    }

    @Bean
    UserRepository userRepository() {
        return new InMemoryUserRepository();
    }

    @Bean
    UserHandler userHandler(UserRepository userRepository) {
        return new UserHandler(userRepository);
    }

}
