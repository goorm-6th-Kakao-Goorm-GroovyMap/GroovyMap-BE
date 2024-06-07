package aespa.groovymap.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Place {
    private String name;
    private String address;
    private String rentalFee;
    private String capacity;
    private String performanceHours;
    private String description;
}
