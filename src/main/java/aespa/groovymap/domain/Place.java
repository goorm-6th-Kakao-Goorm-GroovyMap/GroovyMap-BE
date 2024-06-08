package aespa.groovymap.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Place {
    private String name;
    private String region;
    private String address;
    private String phoneNumber;
    private String rentalFee;
    private String capacity;
    private String hours;
    private String description;
}
