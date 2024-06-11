package aespa.groovymap.domain.post;

import aespa.groovymap.domain.Comment;
import aespa.groovymap.domain.Member;
import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Member author;

    private String title;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;
    private Integer likesCount;
    private Integer savesCount;
    private Integer viewCount;

    @OneToMany(mappedBy = "commentPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    private ZonedDateTime timestamp;
}
