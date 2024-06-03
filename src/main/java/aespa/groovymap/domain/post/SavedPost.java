package aespa.groovymap.domain.post;

import aespa.groovymap.domain.MemberContent;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SavedPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Post post;
    private MemberContent memberContent;
}
