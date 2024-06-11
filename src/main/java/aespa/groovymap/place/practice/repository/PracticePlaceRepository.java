package aespa.groovymap.place.practice.repository;

import aespa.groovymap.domain.post.PracticePlacePost;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PracticePlaceRepository extends JpaRepository<PracticePlacePost, Long> {

    @Override
    List<PracticePlacePost> findAll();

    @Override
    <S extends PracticePlacePost> S save(S entity);

    @Override
    Optional<PracticePlacePost> findById(Long aLong);
}
