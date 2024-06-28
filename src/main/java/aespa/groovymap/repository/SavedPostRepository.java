package aespa.groovymap.repository;

import aespa.groovymap.domain.MemberContent;
import aespa.groovymap.domain.post.Post;
import aespa.groovymap.domain.post.SavedPost;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedPostRepository extends JpaRepository<SavedPost, Long> {
    List<SavedPost> findBySavedMemberContent(MemberContent memberContent); // 특정 회원의 모든 저장 기록을 조회

    List<SavedPost> findBySavedPost(Post savedPost); // 특정 게시글에 대한 회원들의 모든 저장 기록을 조회
}
