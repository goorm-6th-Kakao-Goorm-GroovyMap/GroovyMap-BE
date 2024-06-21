package aespa.groovymap.freepost.repository;

import aespa.groovymap.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreePostRepository extends JpaRepository<Member, Long> {
}
