package aespa.groovymap.freepost.repository;

import aespa.groovymap.domain.Category;
import aespa.groovymap.domain.post.FreePost;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FreePostRepository extends JpaRepository<FreePost, Long> {

    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select p from FreePost p where p.id = :id")
    Optional<FreePost> findByIdWithImages(Long id); // 게시글 id로 게시글 조회(이미지 포함)

    List<FreePost> findByCategory(Category category); // 분야별로 조회

    @Modifying
    @Query("update FreePost p set p.viewCount = p.viewCount + 1 where p.id = :id")
    int updateViews(@Param("id") Long id);

    @Modifying
    @Query("update FreePost p set p.savesCount = p.savesCount + 1 where p.id = :id")
    int updateSaves(@Param("id") Long id);

    @Modifying
    @Query("update FreePost p set p.likesCount = p.likesCount + 1 where p.id = :id")
    int updateLikes(@Param("id") Long id);
}
