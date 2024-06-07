package aespa.groovymap.place.performance.repository;

import aespa.groovymap.domain.post.PerformancePlacePost;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformancePlaceRepository extends JpaRepository<PerformancePlacePost, Long> {
    @Override
    List<PerformancePlacePost> findAll();

    @Override
    <S extends PerformancePlacePost> S save(S entity);
}
