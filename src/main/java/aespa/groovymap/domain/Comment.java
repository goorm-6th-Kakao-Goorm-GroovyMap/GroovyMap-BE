package aespa.groovymap.domain;

import aespa.groovymap.domain.post.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.ZonedDateTime;

import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    // 미사용 postId제거 실제 사용되는 Post의 id는 commentPost안에 존재
    //private Long postId;
}
