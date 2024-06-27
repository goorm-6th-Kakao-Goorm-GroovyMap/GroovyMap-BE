package aespa.groovymap.mypage.controller;

import aespa.groovymap.config.SessionConstants;
import aespa.groovymap.mypage.dto.MyPagePerformance.MyPagePerformanceRequestDto;
import aespa.groovymap.mypage.dto.MyPagePerformance.MyPagePerformanceResponseDto;
import aespa.groovymap.mypage.service.MyPagePerformanceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MyPagePerformanceController {

    private final MyPagePerformanceService myPagePerformanceService;

    @PostMapping("/mypage/performance/write")
    public ResponseEntity writeMyPagePerformance(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId,
            @RequestBody MyPagePerformanceRequestDto myPagePerformanceRequestDto) {
        log.info("마이 페이지 공연 기록 작성 요청 : {}", memberId);
        if (memberId != null) {
            myPagePerformanceService.writeMyPagePerformance(myPagePerformanceRequestDto, memberId);
            return ResponseEntity.ok("");
        }
        return ResponseEntity.badRequest().body("need login");
    }

    @GetMapping("/mypage/performance/{nickname}")
    public ResponseEntity getMyPagePerformances(@PathVariable("nickname") String nickname) {
        log.info("마이 페이지 공연 기록 목록 요청, 닉네임 : {}", nickname);
        List<MyPagePerformanceResponseDto> myPagePerformanceResponseDtos
                = myPagePerformanceService.getMyPagePerformances(nickname);
        return ResponseEntity.ok(myPagePerformanceResponseDtos);
    }
}
