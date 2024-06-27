package aespa.groovymap.profile.repository;

import aespa.groovymap.domain.Member;
import aespa.groovymap.domain.Profile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByMember(Member member);

}
