package ca.nbc.digital.reatil.geekshow;

import ca.nbc.digital.reatil.geekshow.domain.Address;
import ca.nbc.digital.reatil.geekshow.domain.User;
import ca.nbc.digital.reatil.geekshow.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static reactor.core.publisher.Mono.from;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserResourceTest {
    private WebTestClient webTestClient;

    @LocalServerPort
    private int port;

    private final User john = new User("John", "Doe", LocalDate.of(2010, 1, 15));
    private final User joe = new User("Joe", "Blow", LocalDate.of(2003, 1, 15));

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Nested
    @DisplayName("Should create a user")
    @TestMethodOrder(OrderAnnotation.class)
    class UserCreationScenario {

        @Test
        @DisplayName("Given there is no users in the system")
        @Order(1)
        void list() {
            userRepository.deleteAll().block();

            webTestClient.get()
                    .uri("/users")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(User.class).hasSize(0);
        }

        @Test
        @DisplayName("And I create a user")
        @Order(2)
        void create() {
            webTestClient.post()
                    .uri("/users")
                    .bodyValue(john)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectHeader().location("http://localhost:" + port + "/users/" + from(userRepository.findAll()).block().id());
        }

        @Test
        @DisplayName("Then a user is created")
        @Order(3)
        void created() {
            webTestClient.get()
                    .uri("/users")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(User.class).hasSize(1);
        }

        @Test
        @DisplayName("And I should be able to retrieve the user")
        @Order(4)
        void fetch() {
            String userId = from(userRepository.findAll()).block().id();
            webTestClient.get()
                    .uri("/users/{id}", userId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(userId)
                    .jsonPath("$.firstName").isEqualTo(john.firstName())
                    .jsonPath("$.lastName").isEqualTo(john.lastName())
                    .jsonPath("$.birthDate").isEqualTo(john.birthDate().toString());
        }
    }

    @Nested
    @DisplayName("Try to add address to an user")
    @TestMethodOrder(OrderAnnotation.class)
    class UserAddressScenario {

        @Test
        @DisplayName("Allowed if major")
        @Order(2)
        void addAddressForMajor() {
            userRepository.deleteAll().block();
            userRepository.save(joe).block();

            webTestClient.patch()
                    .uri("/users/{id}/addresses", joe.id())
                    .bodyValue(new Address("1234", "Main Street", "Montreal", "Canada", "H3H 3H3"))
                    .exchange()
                    .expectStatus().isCreated()
                    .expectHeader().location("http://localhost:" + port + "/users/" + joe.id());

            webTestClient.get()
                    .uri("/users/{id}", joe.id())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(joe.id())
                    .jsonPath("$.address.street").isEqualTo(joe.address().street())
                    .jsonPath("$.address.zip").isEqualTo(joe.address().zip())
                    .jsonPath("$.address.city").isEqualTo(joe.address().city())
                    .jsonPath("$.address.state").isEqualTo(joe.address().state())
                    .jsonPath("$.address.country").isEqualTo(joe.address().country());
        }

        @Test
        @DisplayName("Forbidden if minor")
        @Order(3)
        void addAddressForMinor() {
            userRepository.save(john).block();

            webTestClient.patch()
                    .uri("/users/{id}/addresses", john.id())
                    .bodyValue(new Address("1234", "Main Street", "Montreal", "Canada", "H3H 3H3"))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectHeader().contentType(APPLICATION_PROBLEM_JSON)
                    .expectBody()
                        .jsonPath("$.title").isEqualTo("Bad Request")
                        .jsonPath("$.status").isEqualTo(400)
                        .jsonPath("$.detail").isEqualTo("User must be 18 years old to add an address")
            ;

        }

    }
}
