package aespa.groovymap.domain.post;

import aespa.groovymap.domain.Category;
import aespa.groovymap.domain.Coordinate;
import aespa.groovymap.domain.Type;
import jakarta.persistence.Entity;

@Entity
public class PRPost extends Post {
    private Category category;
    private Coordinate coordinate;
    private Type type;
}
