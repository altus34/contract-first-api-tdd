package ca.nbc.digital.reatil.geekshow;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ProblemDetail;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static java.util.Collections.singletonList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static org.springframework.web.reactive.function.server.RequestPredicates.all;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@RestControllerAdvice
public final class ReactiveExceptionHandler extends AbstractErrorWebExceptionHandler {
    public ReactiveExceptionHandler(ErrorAttributes errorAttributes, WebProperties webProperties,
                                    ApplicationContext applicationContext, ObjectMapper encoder) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        Jackson2JsonEncoder ja = new Jackson2JsonEncoder(encoder);
        setMessageWriters(singletonList(new EncoderHttpMessageWriter<>(ja)));
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return route(all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(BAD_REQUEST, error.getMessage());

        return ServerResponse.status(BAD_REQUEST)
                .contentType(APPLICATION_PROBLEM_JSON)
                .body(fromValue(problem));
    }
}
