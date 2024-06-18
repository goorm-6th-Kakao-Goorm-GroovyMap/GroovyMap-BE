package aespa.groovymap.profile.repository;

import aespa.groovymap.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
