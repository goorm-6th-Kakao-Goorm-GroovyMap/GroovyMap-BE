package aespa.groovymap.domain.post;

import aespa.groovymap.domain.Category;
import aespa.groovymap.domain.Coordinate;
import jakarta.persistence.Entity;

@Entity
public class CollaborationPlacePost extends Post {
    private Category category;
    private Coordinate coordinate;
}
