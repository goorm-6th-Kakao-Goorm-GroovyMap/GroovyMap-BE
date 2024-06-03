package aespa.groovymap.domain.post;

import aespa.groovymap.domain.Category;
import aespa.groovymap.domain.Coordinate;
import jakarta.persistence.Entity;

@Entity
public class PromotionPost extends Post {
    private Coordinate coordinate;
    private Category category;
}
