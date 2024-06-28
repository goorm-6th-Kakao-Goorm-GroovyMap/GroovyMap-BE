package aespa.groovymap.mypage.repository;

import aespa.groovymap.domain.post.MyPagePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyPagePostRepository extends JpaRepository<MyPagePost, Long> {

    @Override
    <S extends MyPagePost> S save(S entity);
}
