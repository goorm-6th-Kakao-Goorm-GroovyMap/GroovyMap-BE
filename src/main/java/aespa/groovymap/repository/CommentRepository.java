package aespa.groovymap.repository;

import aespa.groovymap.domain.Comment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Override
    <S extends Comment> S save(S entity);

    @Override
    Optional<Comment> findById(Long aLong);
}
