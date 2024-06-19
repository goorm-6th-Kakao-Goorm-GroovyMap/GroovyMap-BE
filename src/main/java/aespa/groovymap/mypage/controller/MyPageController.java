package aespa.groovymap.mypage.controller;

import aespa.groovymap.config.SessionConstants;
import aespa.groovymap.mypage.dto.MyPageInfoDto;
import aespa.groovymap.mypage.dto.MyPagePhotosDto;
import aespa.groovymap.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping("/mypage")
    public ResponseEntity getMyPageInfo(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId) {
        log.info("마이 페이지 정보 요청");
        if (memberId != null) {
            MyPageInfoDto myPageInfoDto = myPageService.getMyPageInfo(memberId);
            return ResponseEntity.ok(myPageInfoDto);
        }
        return ResponseEntity.badRequest().body("need login");
    }

    @GetMapping("/mypage/photo/{memberId}")
    public ResponseEntity getMyPagePhotos(@PathVariable("memberId") Long memberId) {
        log.info("마이 페이지 게시물 목록 요청");
        MyPagePhotosDto myPagePhotosDto = myPageService.getMyPagePhotos(memberId);
        return ResponseEntity.ok(myPagePhotosDto);
    }
}
