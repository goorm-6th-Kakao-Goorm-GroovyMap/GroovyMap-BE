package aespa.groovymap.mypage.controller;

import aespa.groovymap.config.SessionConstants;
import aespa.groovymap.mypage.dto.MyPageInfoDto;
import aespa.groovymap.mypage.dto.MyPageInfoUpdateRequestDto;
import aespa.groovymap.mypage.dto.MyPageInfoUpdateResponseDto;
import aespa.groovymap.mypage.service.MyPageService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping("/mypage/{nickname}")
    public ResponseEntity getMyPageInfo(@PathVariable("nickname") String nickname) {
        log.info("마이 페이지 정보 요청");
        MyPageInfoDto myPageInfoDto = myPageService.getMyPageInfo(nickname);
        return ResponseEntity.ok(myPageInfoDto);
    }

    @PatchMapping("/member/update")
    public ResponseEntity updateMyPageInfo(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId,
            @ModelAttribute MyPageInfoUpdateRequestDto myPageInfoUpdateRequestDto) throws IOException {
        log.info("마이 페이지 정보 수정 요청 : {}", memberId);
        log.info("{}", myPageInfoUpdateRequestDto.getProfileImage());
        if (memberId != null) {
            MyPageInfoUpdateResponseDto myPageInfoUpdateResponseDto = myPageService.updateMyPageInfo(memberId,
                    myPageInfoUpdateRequestDto);
            return ResponseEntity.ok(myPageInfoUpdateResponseDto);
        }
        return ResponseEntity.badRequest().body("need login");

    }
}
