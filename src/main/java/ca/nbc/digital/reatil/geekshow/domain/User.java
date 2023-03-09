package ca.nbc.digital.reatil.geekshow.domain;

import com.fasterxml.jackson.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.UUID.*;

@JsonInclude(NON_NULL)
public class User {

    @JsonProperty("id")
    private final String id;
    @JsonProperty("firstName")
    private final String firstName;

    @JsonProperty("lastName")
    private final String lastName;
    @JsonFormat(pattern="yyyy-MM-dd")
    private final LocalDate birthDate;
    @JsonIgnore
    private final List<Address> addresses = new ArrayList<>();

    @JsonProperty("address")
    private Address address;

    @JsonCreator
    public User(@JsonProperty("firstName")  String firstName,
                @JsonProperty("lastName") String lastName,
                @JsonProperty("birthDate") LocalDate birthDate) {
        this.id = randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    public String id() {
        return id;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public LocalDate birthDate() {
        return birthDate;
    }

    public Address address() {
        return address;
    }

    public boolean hasMajority() {
        return birthDate.plusYears(18).isBefore(LocalDate.now());
    }

    public void add(Address address) {
        addresses.add(address);
        this.address = address;
    }
}
