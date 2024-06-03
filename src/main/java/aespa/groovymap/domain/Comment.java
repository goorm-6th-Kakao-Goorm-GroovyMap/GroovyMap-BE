package aespa.groovymap.domain;

import aespa.groovymap.domain.post.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.ZonedDateTime;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Member author;
    private String content;
    private Post post;
    private ZonedDateTime timestamp;
}
