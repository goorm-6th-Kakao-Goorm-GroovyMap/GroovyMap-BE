package aespa.groovymap.mypage.controller;

import aespa.groovymap.config.SessionConstants;
import aespa.groovymap.mypage.dto.MyPagePhoto.MyPageOnePhotoDto;
import aespa.groovymap.mypage.dto.MyPagePhoto.MyPagePhotoCommentDto;
import aespa.groovymap.mypage.dto.MyPagePhoto.MyPagePhotoWriteDto;
import aespa.groovymap.mypage.dto.MyPagePhoto.MyPagePhotosDto;
import aespa.groovymap.mypage.service.MyPagePhotoService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MyPagePhotoController {

    private final MyPagePhotoService myPagePhotoService;

    @GetMapping("/mypage/photo/{nickname}")
    public ResponseEntity getMyPagePhotos(@PathVariable("nickname") String nickname) {
        log.info("마이 페이지 게시물 목록 요청");
        MyPagePhotosDto myPagePhotosDto = myPagePhotoService.getMyPagePhotos(nickname);
        return ResponseEntity.ok(myPagePhotosDto);
    }

    @PostMapping("/mypage/photo/write")
    public ResponseEntity writeMyPagePhoto(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId,
            @ModelAttribute MyPagePhotoWriteDto myPagePhotoWriteDto) throws IOException {
        log.info("마이 페이지 게시물 작성 요청 : {}", memberId);
        if (memberId != null) {
            myPagePhotoService.writeMyPagePhoto(myPagePhotoWriteDto, memberId);
            return ResponseEntity.ok("");
        }
        return ResponseEntity.badRequest().body("need login");
    }

    @GetMapping("/mypage/photo/{nickname}/{postId}")
    public ResponseEntity getMyPagePhoto(@PathVariable("postId") Long postId) {
        log.info("마이 페이지 게시글 하나 요청 : {}", postId);
        MyPageOnePhotoDto myPageOnePhotoDto = myPagePhotoService.getMyPagePhoto(postId);
        return ResponseEntity.ok(myPageOnePhotoDto);
    }

    @DeleteMapping("/mypage/photo/{postId}")
    public ResponseEntity deleteMyPagePhoto(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId,
            @PathVariable("postId") Long postId) {
        log.info("마이 페이지 게시글 삭제 요청 : {}", postId);
        if (memberId != null) {
            myPagePhotoService.deleteMyPagePhoto(memberId, postId);
            return ResponseEntity.ok("");
        }
        return ResponseEntity.badRequest().body("need login");
    }

    @PostMapping("/mypage/photo/{nickname}/{postId}/like")
    public ResponseEntity likeMyPagePhoto(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId,
            @PathVariable("postId") Long postId) {
        log.info("마이 페이지 게시글 좋아요 요청, 요청 member : {}", memberId);
        if (memberId != null) {
            Boolean likedSuccess = myPagePhotoService.likeMyPagePhoto(memberId, postId);

            if (likedSuccess) {
                return ResponseEntity.ok("post like success");
            } else {
                return ResponseEntity.badRequest().body("already liked post");
            }
        }
        return ResponseEntity.badRequest().body("need login");
    }

    @PostMapping("/mypage/photo/{postId}/comments")
    public ResponseEntity writeComment(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId,
            @PathVariable("postId") Long postId, @RequestBody MyPagePhotoCommentDto myPagePhotoCommentDto) {

        log.info("마이 페이지 게시글에 댓글 요청, 작성자 id : {}, 게시글 id : {}", memberId, postId);

        myPagePhotoService.writeComment(memberId, postId, myPagePhotoCommentDto.getText());

        return ResponseEntity.ok("");
    }

    @DeleteMapping("/mypage/photo/{postId}/comments/{commentId}")
    public ResponseEntity deleteComment(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId,
            @PathVariable("commentId") Long commentId) {
        log.info("마이 페이지 게시글 댓글 삭제 요청 : {}", commentId);
        if (memberId != null) {
            myPagePhotoService.deleteMyPagePhotoComment(memberId, commentId);
            return ResponseEntity.ok("");
        }
        return ResponseEntity.badRequest().body("need login");
    }
}
