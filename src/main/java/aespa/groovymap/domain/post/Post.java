package aespa.groovymap.domain.post;

import aespa.groovymap.domain.Comment;
import aespa.groovymap.domain.Member;
import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Member author;
    private String content;
    private Integer likesCount;
    private Integer savesCount;
    private List<Comment> comments;
    private ZonedDateTime timestamp;
    private String title;
    private Integer viewCount;
}
