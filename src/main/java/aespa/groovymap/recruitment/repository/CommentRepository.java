package aespa.groovymap.recruitment.repository;

import aespa.groovymap.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId); // postId 필드가 존재하는 경우
}
