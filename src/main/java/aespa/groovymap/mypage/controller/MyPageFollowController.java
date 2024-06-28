package aespa.groovymap.mypage.controller;

import aespa.groovymap.config.SessionConstants;
import aespa.groovymap.mypage.dto.MyPageFollow.MyPageFollowDto;
import aespa.groovymap.mypage.service.MyPageFollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MyPageFollowController {

    private final MyPageFollowService myPageFollowService;

    @PostMapping("/mypage/following/{nickname}")
    public ResponseEntity followOtherMember(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId,
            @PathVariable("nickname") String nickname) {
        log.info("팔로잉 요청 : {} to {} ", memberId, nickname);
        if (memberId != null) {
            MyPageFollowDto myPageFollowDto = myPageFollowService.followOtherMember(memberId, nickname);
            return ResponseEntity.ok(myPageFollowDto);
        }
        return ResponseEntity.badRequest().body("need login");
    }
}
