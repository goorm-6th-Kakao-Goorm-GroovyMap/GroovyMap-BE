package aespa.groovymap.domain;

import aespa.groovymap.domain.post.LikedPost;
import aespa.groovymap.domain.post.MyPagePerformancePost;
import aespa.groovymap.domain.post.MyPagePost;
import aespa.groovymap.domain.post.SavedPost;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Entity
public class MemberContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String introduction;
    private MultipartFile profileImage;
    private List<MyPagePost> myPagePosts;
    private List<MyPagePerformancePost> myPagePerformancePosts;
    private List<SavedPost> savedPosts;
    private List<LikedPost> likedPosts;
}
