package aespa.groovymap.domain.post;

import aespa.groovymap.domain.MemberContent;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class LikedPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Post posdId;
    private MemberContent memberContent;
}
