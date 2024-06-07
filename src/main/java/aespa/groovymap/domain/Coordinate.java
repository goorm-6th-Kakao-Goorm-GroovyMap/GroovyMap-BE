package aespa.groovymap.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Coordinate {
    private Double latitude;
    private Double longitude;
}
