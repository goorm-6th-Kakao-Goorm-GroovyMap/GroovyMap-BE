package aespa.groovymap.mypage.repository;

import aespa.groovymap.domain.post.MyPagePerformancePost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyPagePerformancePostRepository extends JpaRepository<MyPagePerformancePost, Long> {

    @Override
    <S extends MyPagePerformancePost> S save(S entity);
}
