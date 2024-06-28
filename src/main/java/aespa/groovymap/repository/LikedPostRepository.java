package aespa.groovymap.repository;

import aespa.groovymap.domain.post.LikedPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikedPostRepository extends JpaRepository<LikedPost, Long> {
    @Override
    <S extends LikedPost> S save(S entity);
}
