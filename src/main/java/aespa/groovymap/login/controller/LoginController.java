package aespa.groovymap.login.controller;

import aespa.groovymap.config.SessionConstants;
import aespa.groovymap.domain.Member;
import aespa.groovymap.login.dto.LoginDto;
import aespa.groovymap.login.dto.MemberInfoDto;
import aespa.groovymap.login.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        log.info("로그인 시도");
        Member loginMember = loginService.login(loginDto.getEmail(), loginDto.getPassword());

        if (loginMember == null) {
            return ResponseEntity.badRequest().body(false);
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConstants.MEMBER_ID, loginMember.getId());
        return ResponseEntity.ok(true);
    }

    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok().body("logout complete");
    }

    @GetMapping("/memberInfo")
    public ResponseEntity getMemberInfo(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId) {
        log.info("memberInfo request come : {}", memberId);
        if (memberId != null) {
            MemberInfoDto memberInfoDto = loginService.getMemberInfo(memberId);
            return ResponseEntity.ok(memberInfoDto);
        } else {
            return ResponseEntity.ok("need login");
        }
    }

    @GetMapping("/login/test")
    public ResponseEntity loginTest(
            @SessionAttribute(name = SessionConstants.MEMBER_ID, required = false) Long memberId) {
        if (memberId != null) {
            return ResponseEntity.ok(memberId);
        } else {
            return ResponseEntity.ok("session end");
        }
    }
}
