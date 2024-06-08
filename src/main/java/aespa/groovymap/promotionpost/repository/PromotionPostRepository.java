package aespa.groovymap.promotionpost.repository;

import aespa.groovymap.domain.post.PromotionPost;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PromotionPostRepository extends JpaRepository<PromotionPost, Long> {

    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select p from PromotionPost p where p.id = :id")
    Optional<PromotionPost> findByIdWithImages(Long id); // 게시글 id로 게시글 조회(이미지 포함)

//    List<PromotionPost> findByCategory(String category); // 분야별로 조회
//
//    @Query("select p from PromotionPost p where p.region = :region")
//    List<PromotionPost> findByRegion(String region); // 지역별로 조회
//
//    @Query("select p from PromotionPost p where p.category = :category and p.region = :region")
//    List<PromotionPost> findByCategoryAndRegion(String category, String region); // 분야와 지역별로 조회
}

