package aespa.groovymap.mypage.controller;

import aespa.groovymap.config.SessionConstants;
import aespa.groovymap.mypage.dto.MyPagePhoto.MyPagePhotoCommentDto;
import aespa.groovymap.mypage.service.MyPagePhotoOtherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MyPagePhotoOtherController {

    private final MyPagePhotoOtherService myPagePhotoOtherService;

    @PostMapping("/mypage/photo/{postId}/like")
    public ResponseEntity likeMyPagePhoto(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId,
            @PathVariable("postId") Long postId) {
        log.info("마이 페이지 게시글 좋아요 요청, 요청 member : {}", memberId);
        if (memberId != null) {
            Boolean likedSuccess = myPagePhotoOtherService.likeMyPagePhoto(memberId, postId);

            if (likedSuccess) {
                return ResponseEntity.ok("post like success");
            } else {
                return ResponseEntity.badRequest().body("already liked post");
            }
        }
        return ResponseEntity.badRequest().body("need login");
    }

    @PostMapping("/mypage/photo/{postId}/unlike")
    public ResponseEntity unlikeMyPagePhoto(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId,
            @PathVariable("postId") Long postId) {
        log.info("마이 페이지 게시글 좋아요 취소 요청, 요청 member : {}", memberId);
        if (memberId != null) {
            Boolean likedSuccess = myPagePhotoOtherService.unlikeMyPagePhoto(memberId, postId);

            if (likedSuccess) {
                return ResponseEntity.ok("post unlike success");
            } else {
                return ResponseEntity.badRequest().body("not liked post");
            }
        }
        return ResponseEntity.badRequest().body("need login");
    }

    @PostMapping("/mypage/photo/{postId}/comments")
    public ResponseEntity writeComment(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId,
            @PathVariable("postId") Long postId, @RequestBody MyPagePhotoCommentDto myPagePhotoCommentDto) {

        log.info("마이 페이지 게시글에 댓글 요청, 작성자 id : {}, 게시글 id : {}", memberId, postId);

        myPagePhotoOtherService.writeMyPagePhotoComment(memberId, postId, myPagePhotoCommentDto.getText());

        return ResponseEntity.ok("");
    }

    @DeleteMapping("/mypage/photo/{postId}/comments/{commentId}")
    public ResponseEntity deleteComment(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId,
            @PathVariable("commentId") Long commentId) {
        log.info("마이 페이지 게시글 댓글 삭제 요청 : {}", commentId);
        if (memberId != null) {
            myPagePhotoOtherService.deleteMyPagePhotoComment(memberId, commentId);
            return ResponseEntity.ok("");
        }
        return ResponseEntity.badRequest().body("need login");
    }
}
