package ca.nbc.digital.reatil.geekshow.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.UUID.randomUUID;

@JsonInclude(NON_NULL)
public class Address {
    @JsonProperty("id")
    private final String id;
    @JsonProperty("street")
    private final String street;
    @JsonProperty("city")
    private final String city;
    @JsonProperty("country")
    private final String country;
    @JsonProperty("state")
    private final String state;
    @JsonProperty("zip")
    private final String zip;

    public Address(@JsonProperty("street") String street,
                   @JsonProperty("zip") String zip,
                   @JsonProperty("city") String city,
                   @JsonProperty("country") String country,
                   @JsonProperty("state") String state) {
        this.id = randomUUID().toString();
        this.street = street;
        this.zip = zip;
        this.city = city;
        this.country = country;
        this.state = state;
    }

    public String id() {
        return id;
    }

    public String street() {
        return street;
    }

    public String city() {
        return city;
    }

    public String country() {
        return country;
    }

    public String state() {
        return state;
    }

    public String zip() {
        return zip;
    }
}
