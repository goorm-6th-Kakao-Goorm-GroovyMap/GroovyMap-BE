package aespa.groovymap.repository;

import aespa.groovymap.domain.MemberContent;
import aespa.groovymap.domain.post.SavedPost;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedPostRepository extends JpaRepository<SavedPost, Long> {
    List<SavedPost> findBySavedMemberContent(MemberContent memberContent);
}
