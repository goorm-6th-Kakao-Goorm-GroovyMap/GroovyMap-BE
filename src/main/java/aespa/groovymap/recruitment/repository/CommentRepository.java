package aespa.groovymap.recruitment.repository;

import aespa.groovymap.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 기존 id값을 기존 id값이 아닌 post의 Id값으로 변경
    @Query("SELECT e FROM Comment e WHERE e.commentPost.id = :postId")
    List<Comment> findByPostId(Long postId); // postId 필드가 존재하는 경우
}
