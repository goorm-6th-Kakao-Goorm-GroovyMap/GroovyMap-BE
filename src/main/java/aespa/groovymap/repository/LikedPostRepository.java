package aespa.groovymap.repository;

import aespa.groovymap.domain.MemberContent;
import aespa.groovymap.domain.post.LikedPost;
import aespa.groovymap.domain.post.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikedPostRepository extends JpaRepository<LikedPost, Long> {
    List<LikedPost> findByLikedMemberContent(MemberContent memberContent); // 특정 회원의 모든 좋아요 기록을 조회

    List<LikedPost> findByLikedPost(Post likedPost); // 특정 게시글에 대한 회원들의 모든 좋아요 기록을 조회

    @Override
    <S extends LikedPost> S save(S entity);
}
