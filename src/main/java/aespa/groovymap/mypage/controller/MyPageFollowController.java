package aespa.groovymap.mypage.controller;

import aespa.groovymap.config.SessionConstants;
import aespa.groovymap.mypage.dto.MyPageFollow.MyPageFollowDto;
import aespa.groovymap.mypage.dto.MyPageFollow.MyPageFollowResponseDto;
import aespa.groovymap.mypage.service.MyPageFollowService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    @DeleteMapping("/mypage/unfollow/{nickname}")
    public ResponseEntity unfollowOtherMember(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId,
            @PathVariable("nickname") String nickname) {
        log.info("팔로잉 취소 요청 : {} to {} ", memberId, nickname);
        if (memberId != null) {
            Boolean unfollowedSuccess = myPageFollowService.unfollowOtherMember(memberId, nickname);
            if (unfollowedSuccess) {
                return ResponseEntity.ok("unfollow success");
            } else {
                return ResponseEntity.badRequest().body("not followed yet");
            }
        }
        return ResponseEntity.badRequest().body("need login");
    }

    @GetMapping("/mypage/{nickname}/followers")
    public ResponseEntity getFollowers(@PathVariable("nickname") String nickname) {
        log.info("나의 팔로워 목록 요청, 닉네임 : {}", nickname);
        List<MyPageFollowResponseDto> myPageFollowResponseDtos = myPageFollowService.getFollowers(nickname);
        return ResponseEntity.ok(myPageFollowResponseDtos);
    }

    @GetMapping("/mypage/{nickname}/following")
    public ResponseEntity getFollowing(@PathVariable("nickname") String nickname) {
        log.info("나의 팔로워 목록 요청, 닉네임 : {}", nickname);
        List<MyPageFollowResponseDto> myPageFollowResponseDtos = myPageFollowService.getFollowing(nickname);
        return ResponseEntity.ok(myPageFollowResponseDtos);
    }
}
