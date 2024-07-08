package aespa.groovymap.recruitment.repository;

import aespa.groovymap.domain.post.RecruitTeamMemberPost;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecruitTeamMemberRepository extends JpaRepository<RecruitTeamMemberPost, Long> {

    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select p from FreePost p where p.id = :id")
    Optional<RecruitTeamMemberPost> findByIdWithImages(Long id); // 게시글 id로 게시글 조회(이미지 포함)

    @Modifying
    @Query("update RecruitTeamMemberPost p set p.viewCount = p.viewCount + 1 where p.id = :id")
    int updateViews(@Param("id") Long id);

    @Modifying
    @Query("update RecruitTeamMemberPost p set p.savesCount = p.savesCount + 1 where p.id = :id")
    int updateSaves(@Param("id") Long id);

    @Modifying
    @Query("update RecruitTeamMemberPost p set p.likesCount = p.likesCount + 1 where p.id = :id")
    int updateLikes(@Param("id") Long id);
}


