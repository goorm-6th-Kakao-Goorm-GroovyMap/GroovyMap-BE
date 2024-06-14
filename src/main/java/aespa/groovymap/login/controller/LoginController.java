package aespa.groovymap.login.controller;

import aespa.groovymap.config.SessionConstants;
import aespa.groovymap.domain.Member;
import aespa.groovymap.login.dto.LoginDto;
import aespa.groovymap.login.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity login(LoginDto loginDto, HttpServletRequest request) {
        Member loginMember = loginService.login(loginDto.getEmail(), loginDto.getPassword());

        if (loginMember == null) {
            return ResponseEntity.badRequest().body(false);
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConstants.MEMBER_ID, loginMember.getId());
        return ResponseEntity.ok(true);
    }
}
