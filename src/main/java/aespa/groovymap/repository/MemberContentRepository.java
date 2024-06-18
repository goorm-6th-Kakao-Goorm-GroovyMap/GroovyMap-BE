package aespa.groovymap.repository;

import aespa.groovymap.domain.MemberContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberContentRepository extends JpaRepository<MemberContent, Long> {

    @Override
    <S extends MemberContent> S save(S entity);
}
