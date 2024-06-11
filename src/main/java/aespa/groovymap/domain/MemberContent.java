package aespa.groovymap.domain;

import aespa.groovymap.domain.post.LikedPost;
import aespa.groovymap.domain.post.MyPagePerformancePost;
import aespa.groovymap.domain.post.MyPagePost;
import aespa.groovymap.domain.post.SavedPost;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Entity
@Getter
@Setter
public class MemberContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String introduction;
//    private MultipartFile profileImage;

    @OneToOne(mappedBy = "memberContent")
    private Member member;

    @OneToMany(mappedBy = "myPageMemberContent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MyPagePost> myPagePosts;

    @OneToMany(mappedBy = "myPagePerformanceMemberContent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MyPagePerformancePost> myPagePerformancePosts;

    @OneToMany(mappedBy = "savedMemberContent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavedPost> savedPosts;

    @OneToMany(mappedBy = "likedMemberContent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikedPost> likedPosts;
}
