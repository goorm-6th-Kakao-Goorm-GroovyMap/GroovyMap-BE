package aespa.groovymap.oauth.controller;

import aespa.groovymap.oauth.dto.KakaoLoginResponseDto;
import aespa.groovymap.oauth.service.OAuthService;
import aespa.groovymap.oauth.util.KakaoUtil;
import aespa.groovymap.register.dto.RegisterRequestDto;
import aespa.groovymap.register.dto.RegisterResponseDto;
import aespa.groovymap.register.dto.User;
import aespa.groovymap.register.service.RegisterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequiredArgsConstructor
public class OAuthController {

    private final KakaoUtil kakaoUtil;
    private final OAuthService oauthService;
    private final RegisterService registerService;

    @GetMapping("/login/kakao")
    public String moveToKakaoLogin() {
        String location = kakaoUtil.getKakaoUrl();

        return "redirect:" + location;
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity loginByKakao(@RequestParam("code") String code, HttpServletRequest request) {
        KakaoLoginResponseDto kakaoLoginResponseDto = oauthService.loginByKakao(code, request);
        return ResponseEntity.ok(kakaoLoginResponseDto);
    }

    @GetMapping("/login/oauth/register")
    public ResponseEntity oauthRegister(@RequestBody RegisterRequestDto registerRequestDto) {
        log.info("소셜 로그인 회원 가입");
        User user = registerService.register(registerRequestDto);
        RegisterResponseDto registerResponseDto = new RegisterResponseDto();
        registerResponseDto.setUser(user);
        if (user != null) {
            log.info("소셜 로그인 회원 가입 성공");
            registerResponseDto.setMessage("소셜 로그인 회원가입에 성공했습니다!");
            registerResponseDto.setResult(true);
            return ResponseEntity.ok(registerResponseDto);
        } else {
            log.info("소셜 로그인 회원 가입 실패");
            registerResponseDto.setMessage("소셜 로그인 회원가입에 실패했습니다!");
            registerResponseDto.setResult(false);
            return ResponseEntity.status(409).body(registerResponseDto);
        }
    }
}
