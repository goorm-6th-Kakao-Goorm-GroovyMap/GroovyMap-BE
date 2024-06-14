package aespa.groovymap.recruitment.repository;

import aespa.groovymap.domain.post.RecruitTeamMemberPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruitTeamMemberRepository extends JpaRepository<RecruitTeamMemberPost, Long> {
    @Modifying
    @Query("update RecruitTeamMemberPost p set p.viewCount = p.viewCount + 1 where p.id = :id")
    int updateViews(@Param("id") Long id);

    @Modifying
    @Query("update RecruitTeamMemberPost p set p.likesCount = p.likesCount + 1 where p.id = :id")
    int incrementLikes(@Param("id") Long id);
}
