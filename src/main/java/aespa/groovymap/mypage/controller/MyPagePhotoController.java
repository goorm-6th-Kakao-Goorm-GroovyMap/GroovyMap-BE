package aespa.groovymap.mypage.controller;

import aespa.groovymap.config.SessionConstants;
import aespa.groovymap.mypage.dto.MyPagePhoto.MyPageOnePhotoDto;
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
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MyPagePhotoController {

    private final MyPagePhotoService myPagePhotoService;

    @GetMapping("/mypage/photo/{nickname}")
    public ResponseEntity getMyPagePhotos(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId,
            @PathVariable("nickname") String nickname) {
        log.info("마이 페이지 게시물 목록 요청");
        MyPagePhotosDto myPagePhotosDto = myPagePhotoService.getMyPagePhotos(memberId, nickname);
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
    public ResponseEntity getMyPagePhoto(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId,
            @PathVariable("postId") Long postId) {
        log.info("마이 페이지 게시글 하나 요청 : {}", postId);
        MyPageOnePhotoDto myPageOnePhotoDto = myPagePhotoService.getMyPagePhoto(memberId, postId);
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
}
