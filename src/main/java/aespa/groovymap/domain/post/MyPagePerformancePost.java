package aespa.groovymap.domain.post;

import aespa.groovymap.domain.Coordinate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class MyPagePerformancePost extends Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Coordinate coordinate;
}
