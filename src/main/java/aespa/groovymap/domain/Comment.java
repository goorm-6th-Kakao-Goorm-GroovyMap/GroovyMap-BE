package aespa.groovymap.domain;

import aespa.groovymap.domain.post.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comment_author_id")
    private Member commentAuthor;

    private String content;

    @ManyToOne
    @JoinColumn(name = "comment_post_id")
    private Post commentPost;

    private ZonedDateTime timestamp;
}
