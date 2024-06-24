package aespa.groovymap.repository;

import aespa.groovymap.domain.MemberContent;
import aespa.groovymap.domain.post.LikedPost;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikedPostRepository extends JpaRepository<LikedPost, Long> {
    List<LikedPost> findByLikedMemberContent(MemberContent memberContent);
}
