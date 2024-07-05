package aespa.groovymap.freepost.dto;

import java.util.List;
import lombok.Data;

@Data
public class MyListDto {
    // 회원이 좋아요한 게시글 번호 리스트
    private List<Long> likePostIds;
    // 회원이 저장한 게시글 번호 리스트
    private List<Long> savePostIds;

}