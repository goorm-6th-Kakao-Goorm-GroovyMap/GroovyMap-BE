package aespa.groovymap.domain.post;

import aespa.groovymap.domain.MemberContent;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MyPagePost extends Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    private MultipartFile photoAndVideo;

    @ManyToOne
    @JoinColumn(name = "my_page_member_content_id")
    private MemberContent myPageMemberContent;
}
