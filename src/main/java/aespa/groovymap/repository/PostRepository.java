package aespa.groovymap.repository;

import aespa.groovymap.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Override
    void delete(Post entity);
}
