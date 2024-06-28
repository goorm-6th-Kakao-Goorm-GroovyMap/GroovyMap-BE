package aespa.groovymap.repository;

import aespa.groovymap.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Override
    <S extends Follow> S save(S entity);

    @Override
    void delete(Follow entity);
}
