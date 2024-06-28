package aespa.groovymap.repository;

import aespa.groovymap.domain.Comment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Override
    <S extends Comment> S save(S entity);

    @Override
    Optional<Comment> findById(Long aLong);

    // 기존 id값을 기존 id값이 아닌 post의 Id값으로 변경
    @Query("SELECT e FROM Comment e WHERE e.commentPost.id = :postId")
    List<Comment> findByPostId(Long postId); // postId 필드가 존재하는 경우
}
